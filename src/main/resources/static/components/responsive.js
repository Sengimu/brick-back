//响应式改变
function getCarouselHeight() {
    //循播图框架高度
    let carouselHeight = window.innerWidth * 900 / 2100;
    if (carouselHeight > 900) {
        carouselHeight = 900;
    }
    return carouselHeight;
}