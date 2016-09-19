var exec = require('cordova/exec');

var Slider = function () {

};

Slider.show = function (urls, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "show", urls)
};

Slider.close = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "close",[])
};


module.exports = Slider;

if (!window.plugins) {
    window.plugins = {};
}
if (!window.TaqtileSlider) {
    window.TaqtileSlider = Slider;
}