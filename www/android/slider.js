var exec = require('cordova/exec');

var Slider = function () {

};

Slider.show = function (element, urls, successCallback, errorCallback) {
    var elementParams = {
        "width": element.getBoundingClientRect().width,
        "height": element.getBoundingClientRect().height,
        "top": element.getBoundingClientRect().top
    };

    var data = [elementParams, urls];
    exec(successCallback, errorCallback, "SliderPlugin", "show", data)
};

Slider.close = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "close", [])
};

Slider.destroy = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "destroy", [])
};


module.exports = Slider;

if (!window.plugins) {
    window.plugins = {};
}
if (!window.TaqtileSlider) {
    window.TaqtileSlider = Slider;
}