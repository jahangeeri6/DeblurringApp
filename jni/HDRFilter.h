/*
 * HDRFilter.h
 *
 */

#ifndef HDRFILTER_H_
#define HDRFILTER_H_

#include "Util.h"
#include "ImageFilter.h"
#include "GaussianBlurFilter.h"

class HDRFilter : public ImageFilter {
public:
	HDRFilter(int *_pixels, int _width, int _height): ImageFilter(_pixels, _width, _height) {};
	int* procImage();
};


#endif /* HDRFILTER_H_ */
