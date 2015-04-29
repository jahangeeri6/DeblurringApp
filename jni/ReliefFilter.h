/*
 * ReliefFilter.h
 */

#ifndef RELIEFFILTER_H_
#define RELIEFFILTER_H_

#include "Util.h"
#include "ImageFilter.h"

class ReliefFilter : public ImageFilter {
public:
	ReliefFilter(int *pixels, int width, int height);
	int* procImage();
private:
	int colorCompensation;
};



#endif /* RELIEFFILTER_H_ */
