#include "postgres.h"
#include "fmgr.h"
#include "utils/palloc.h"

#include <stdlib.h>
#include <assert.h>

/* Returns the <ZVALUE>, expected to be a float literal, from a string in the
 * format:
 *
 * 	"CMx<XVALUE>;CMy<YVALUE>;CMz<ZVALUE>"
 *
 * In the btaw.btap_roi_aggregate_info table, this is the format of
 * the 'value' column in entries where the 'description' column is
 * 'CenterOfMass'.
 *
 * If the string in not formatted as noted above, or if otherwise we are
 * not able to extract the ZVALUE, return -1  (all valid values are expected
 * to be non-negative).
 */
/* PG_FUNCTION_INFO_V1(c_mass_z); */
PG_FUNCTION_INFO_V1(c_mass_z);
float4* c_mass_z(PG_FUNCTION_ARGS) {
	/* Get input and prepare variables. */
	text* input = PG_GETARG_TEXT_P(0);
	size_t input_size = VARSIZE(input) - VARHDRSZ;

	/* Data of input is not guaranteed to be null terminated.  Make a
	 * null terminated copy to safely use POSIX C string function.
	 */
	char* null_term_input = palloc(input_size + 1);
	assert(null_term_input != NULL);
	memset(null_term_input, 0, input_size + 1);
	memcpy((void *) null_term_input, (void *) VARDATA(input), input_size);

	/* float4 is defined as float */
	float x_dummy, y_dummy;
	float4 z;
	int found = sscanf(null_term_input, "CMx=%f;CMy=%f;CMz=%f\0", &x_dummy,
			&y_dummy, &z);

	/* Done with our copy of the input. */
	pfree(null_term_input);

	/* Detect error condition. */
	if (found != 3) {
		z = (float4) -1.0;
	}

	PG_RETURN_FLOAT4(z);
}

