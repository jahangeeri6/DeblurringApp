/*
 * SharpenFilter.h
 */

#ifndef SHARPENFILTER_H_
#define SHARPENFILTER_H_

#include "ImageFilter.h"
#include "Util.h"
#include "ColorGetter.h"

class SharpenFilter : public ImageFilter {
public:
	SharpenFilter(int *pixels, int width, int height);
	~SharpenFilter();
	int* procImage();
	int* highBoostSharpen();
};


#endif /* SHARPENFILTER_H_ */
