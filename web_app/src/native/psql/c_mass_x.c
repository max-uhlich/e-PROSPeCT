#include "postgres.h"
#include "fmgr.h"
#include "utils/palloc.h"

#include <stdlib.h>
#include <assert.h>

#ifdef PG_MODULE_MAGIC
PG_MODULE_MAGIC;
#endif

/* Returns the <XVALUE>, expected to be a float literal, from a string in the
 * format:
 *
 * 	"CMx<XVALUE>;CMy<YVALUE>;CMz<ZVALUE>"
 *
 * In the btaw.btap_roi_aggregate_info table, this is the format of
 * the 'value' column in entries where the 'description' column is
 * 'CenterOfMass'.
 *
 * If the string in not formatted as noted above, or if otherwise we are
 * not able to extract the XVALUE, return -1  (all valid values are expected
 * to be non-negative).
 */
/* PG_FUNCTION_INFO_V1(c_mass_x); */
PG_FUNCTION_INFO_V1(c_mass_x);
float4* c_mass_x(PG_FUNCTION_ARGS) {
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
	float4 x;
	int found = sscanf(null_term_input, "CMx=%f;", &x);

	/* Done with our copy of the input. */
	pfree(null_term_input);

	/* Detect error condition. */
	if (found != 1) {
		x = (float4) -1.0;
	}

	PG_RETURN_FLOAT4(x);
}

