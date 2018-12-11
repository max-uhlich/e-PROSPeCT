import datetime
from helpers import date_match, equal_or_after, equal_or_before

def rp(psa_dates, psa_vals, rp_date):
	rp_date = filter(None, rp_date)
	result = []

	if len(psa_dates) > 0:

		for date in rp_date:
			psd = equal_or_after(psa_dates,date)
			if len(psd) != 0:
				for j in range(psa_dates.index(psd),len(psa_dates)):
					cur = psa_vals[j]
					if (j+1)<len(psa_dates):
						nex = psa_vals[j+1]
					else:
						break

					if float(cur) >= 0.2 and float(nex) >= 0.2:
						result.append(['Radical Prostatectomy','Curative',psa_dates[j+1],nex])
						break

	if len(result) > 0:
		return result
	else:
		return [['NULL','NULL','NULL','NULL']]

def rt(psa_dates, psa_vals, rt_start, rt_intent, all_tx):
	all_tx = filter(None, all_tx)
	result = []

	if len(psa_dates) > 0:

		indices = [i for i, x in enumerate(rt_intent) if (x == "Curative" or x == "2")]

		# Add non-curative RT txs to all_tx
		all_tx.extend([rt_start[i] for i in range(len(rt_start)) if i not in indices])
		all_tx = sorted(all_tx, key=lambda x: datetime.datetime.strptime(x, date_match))

		for i in indices:
			date = rt_start[i]
			psd = equal_or_after(psa_dates,date)

			if len(psd) != 0:
				terminus = ''
				if len(all_tx) != 0:
					terminus = equal_or_after(all_tx,date)
				if len(terminus) == 0:
					terminus = datetime.datetime.strftime((datetime.datetime.strptime(psa_dates[-1], date_match) + datetime.timedelta(days=1)),date_match)

				bounce = False
				nadir_set = False
				j = psa_dates.index(psd)
				while j < len(psa_dates):
					cur = psa_vals[j]
					if (j+1) < len(psa_dates):
						nex = psa_vals[j+1]
					else:
						break

					if float(nex)>float(cur):

						nadir = float(cur)
						zenith = nadir + 2

						for k in range((j+1),len(psa_dates)):

							p = psa_vals[k]

							if float(p) <= nadir:
								if nadir_set:
									bounce = True
									nadir_set = False
									#print 'Benign bounce'

								j = k
								break
							elif float(p) >= zenith:
								if not bounce and not nadir_set:
									break_val = p
									break_date = psa_dates[k]
									nadir = 0.5
									zenith = nadir + 2
									nadir_set = True

								if bounce or (k == len(psa_vals)-1) or (datetime.datetime.strptime(psa_dates[k+1], date_match) >= datetime.datetime.strptime(terminus, date_match)):
									if bounce:
										result.append(['Radiation Therapy','Curative',psa_dates[k],p])
									else:
										result.append(['Radiation Therapy','Curative',break_date,break_val])
									break
						if j != k:
							break
					else:
						j = j+1

	if len(result) > 0:
		return result
	else:
		return [['NULL','NULL','NULL','NULL']]

def cryo(psa_dates, psa_vals, cryo_start, cryo_intent):
	result = []

	if len(psa_dates) > 0:

		indices = [i for i, x in enumerate(cryo_intent) if (x == "Primary" or x == "1")]

		for i in indices:
			date = cryo_start[i]
			psd = equal_or_after(psa_dates,date)

			if len(psd) != 0:
				j = psa_dates.index(psd)
				while j < len(psa_dates):
					cur = psa_vals[j]
					if (j+1)<len(psa_dates):
						nex = psa_vals[j+1]
					else:
						break

					if float(nex)>float(cur):
						nadir = float(cur)
						zenith = float(cur) + 2

						for k in range((j+1),len(psa_dates)):
							p = psa_vals[k]

							if float(p) <= nadir:
								j = k
								break
							elif float(p) >= zenith:
								result.append(['Cryotherapy','Primary',psa_dates[k],p])
								break
						if j != k:
							break
					else:
						j = j+1

	if len(result) > 0:
		return result
	else:
		return [['NULL','NULL','NULL','NULL']]

def chemo(psa_dates, psa_vals, chemo_start, chemo_intent):
	result = []

	if len(psa_dates) > 0:

		#intent_dic = {'2': 'Curative', '3': 'Life prolonging', '4': 'Symptom control', '5': 'Adjuvant'}
		#chemo_intent = [intent_dic[x] if x in intent_dic.keys() else '' for x in chemo_intent]
		#indices = [i for i, x in enumerate(chemo_intent) if x in intent_dic.values()]

		indices = [i for i, x in enumerate(chemo_intent) if (x == "Curative" or x == "2")]

		for i in indices:
			date = chemo_start[i]
			psd = equal_or_after(psa_dates,date)

			if len(psd) != 0:
				j = psa_dates.index(psd)
				while j < len(psa_dates):
					cur = psa_vals[j]
					if (j+1)<len(psa_dates):
						nex = psa_vals[j+1]
					else:
						break

					if float(nex)>float(cur):
						nadir = float(cur)
						zenith = float(cur)*1.25

						for k in range((j+1),len(psa_dates)):
							p = psa_vals[k]

							if float(p) <= nadir:
								j = k
								break
							elif float(p) >= zenith:
								result.append(['Chemotherapy','Curative',psa_dates[k],p])
								break
						if j != k:
							break
					else:
						j = j+1

	if len(result) > 0:
		return result
	else:
		return [['NULL','NULL','NULL','NULL']]

def adt(psa_dates, psa_vals, adt_start, adt_stop, adt_intent):
	result = []

	if len(psa_dates) > 0:

		intent_dic = {'1': 'Neo-adjuvant', '2': 'Curative', '3': 'Salvage', '4': 'Metastatic', '5': 'Life prolonging', '6': 'Symptom control', '7': 'Adjuvant'}

		# this line allows tx periods to have no stop date, IE, tx periods which may be in process.
		adt_stop = [psa_dates[-1] if x=='' else x for x in adt_stop]

		for i,date in enumerate(adt_start):
			start = equal_or_after(psa_dates,date)
			stop = equal_or_before(psa_dates,adt_stop[adt_start.index(date)])
			if len(start) != 0 and len(stop) != 0 and psa_dates.index(start) < psa_dates.index(stop):
				j = psa_dates.index(start)

				while j < len(psa_dates) and equal_or_before([psa_dates[j]],stop) != '':
					cur = psa_vals[j]
					if (j+1) < len(psa_dates) and equal_or_before([psa_dates[j+1]],stop) != '':
						nex = psa_vals[j+1]
					else:
						break

					if float(nex)>float(cur):
						nadir = float(cur)
						zenith = float(cur)*1.25

						for k in range((j+1),len(psa_dates)):

							if equal_or_before([psa_dates[k]],stop) == '':
								break

							p = psa_vals[k]

							if float(p) <= nadir:
								j = k
								break
							elif float(p) >= zenith:
								result.append(['Antiandrogen',intent_dic.get(adt_intent[i],'NULL'),psa_dates[k],p])
								break
						if j != k:
							break
					else:
						j = j+1

	if len(result) > 0:
		return result
	else:
		return [['NULL','NULL','NULL','NULL']]

def lhrh(psa_dates, psa_vals, lhrh_start, lhrh_stop, lhrh_intent):
	result = []

	if len(psa_dates) > 0:

		intent_dic = {'1': 'Neo-adjuvant', '2': 'Curative', '3': 'Salvage', '4': 'Metastatic', '5': 'Life prolonging', '6': 'Symptom control', '7': 'Adjuvant'}

		# this line allows tx periods to have no stop date, IE, tx periods which may be in process.
		lhrh_stop = [psa_dates[-1] if x=='' else x for x in lhrh_stop]

		for i,date in enumerate(lhrh_start):
			start = equal_or_after(psa_dates,date)
			stop = equal_or_before(psa_dates,lhrh_stop[lhrh_start.index(date)])
			if len(start) != 0 and len(stop) != 0 and psa_dates.index(start) < psa_dates.index(stop):
				j = psa_dates.index(start)

				while j < len(psa_dates) and equal_or_before([psa_dates[j]],stop) != '':
					cur = psa_vals[j]
					if (j+1) < len(psa_dates) and equal_or_before([psa_dates[j+1]],stop) != '':
						nex = psa_vals[j+1]
					else:
						break

					if float(nex)>float(cur):
						nadir = float(cur)
						zenith = float(nex)
						zenith_date = datetime.datetime.strptime(psa_dates[j+1], date_match)

						for k in range((j+1),len(psa_dates)):

							if equal_or_before([psa_dates[k]],stop) == '':
								break

							p = psa_vals[k]
							p_date = datetime.datetime.strptime(psa_dates[k], date_match)

							if float(p) <= nadir:
								j = k
								break
							elif float(p) > zenith and (p_date - zenith_date).days >= 7:
								result.append(['LHRH',intent_dic.get(lhrh_intent[i],'NULL'),psa_dates[k],p])
								break
						if j != k:
							break
					else:
						j = j+1

	if len(result) > 0:
		return result
	else:
		return [['NULL','NULL','NULL','NULL']]

def last_contact_date(all_dates, all_dates_sqls, all_dates_engs, birth_date):

	result = ['NULL','NULL','NULL','NULL']

	if len(all_dates) > 0:

		result[0] = all_dates[-1]
		result[1] = all_dates_sqls[-1]
		result[2] = all_dates_engs[-1]

		if len(birth_date) > 0:
			d1 = datetime.datetime.strptime(all_dates[-1], date_match)
			d2 = datetime.datetime.strptime(birth_date[0], date_match)
			result[3] = d1.year - d2.year

	return [result]

def age_at_presentation(biopsy_date, birth_date):

	result = []

	for biopsy in biopsy_date:
		if len(birth_date) > 0:
			d1 = datetime.datetime.strptime(biopsy, date_match)
			d2 = datetime.datetime.strptime(birth_date[0], date_match)
			result.append([d1.year - d2.year])

	if len(result) > 0:
		return result
	else:
		return [['NULL']]

def risk(psa_dates, psa_vals, biopsy_date, t_stage, gleason):
	# If a patient has had more than one biopsy, their risk should be the highest for any biopsy
	# This method will only ever return one value from {'Low', 'Intermediate', 'High'}
	# The labels for these categories are: '1, Tx | 2, T0 | 3, T1 | 4, T2 | 5, T3 | 6, T4 | 12, Other'

	# Low Risk = 	1. Clinical stage T1-T2a (we will have to settle for 2 or 3)
	#			AND 2. Gleason score <= 6
	#			AND	3. Pre biopsy PSA < 10

	# Int Risk = 	1. Clinical stage T2b-T2c (so 4)
	#			 OR	2. Gleason score == 7
	#			 OR	3. Pre biopsy 10 <= PSA <= 20

	# Hgh Risk = 	1. Clinical stage T3a or higher (so 5 or 6)
	#			 OR	2. Gleason score >= 8
	#			 OR	3. Pre biopsy PSA > 20

	result = []

	if len(psa_dates) > 0:

		risk_levels = ['Low', 'Intermediate', 'High']

		for i,biopsy in enumerate(biopsy_date):
			stage = [j for j,char in enumerate(t_stage[i]) if char == '1']

			if gleason[i] != '':
				score = float(gleason[i])
			else:
				score = -77

			if len(stage) > 0:
				stage = max(stage)
			else:
				stage = -77

			# Obtain the pre-biopsy psa date
			date = equal_or_before(psa_dates,biopsy)
			if date != '':
				psa = float(psa_vals[psa_dates.index(date)])

				if (stage == 5 or stage == 6) or score >= 8 or psa > 20:
					result.append(2) # High Risk
				elif stage == 4 or score == 7 or (psa >= 10 and psa <= 20):
					result.append(1) # Intermediate Risk
				elif (stage == 2 or stage == 3) and (score >= 0 and score <= 6) and psa < 10:
					result.append(0) # Low Risk

	if len(result) > 0:
		return [[risk_levels[max(result)]]]
	else:
		return [['NULL']]

def pre_psa(psa_dates, psa_vals, biopsy_date, all_tx_full):

	result = []

	if len(psa_dates) > 0:

		# Return one float value, the PSA level closest to and preceding date of biopsy
		for biopsy in biopsy_date:
			date = equal_or_before(psa_dates,biopsy)
			if date != '':
				result.append([psa_vals[psa_dates.index(date)], 'NULL'])

		# Return one float value, the PSA level closest to and preceding the date of first treatment
		if len(all_tx_full) > 0:
			first_tx = all_tx_full[0]
			date = equal_or_before(psa_dates,first_tx)
			if date != '':
				if len(result) > 0:
					result[0][1] = psa_vals[psa_dates.index(date)]
				else:
					result = [['NULL',psa_vals[psa_dates.index(date)]]]

	if len(result) > 0:
		return result
	else:
		return [['NULL','NULL']]