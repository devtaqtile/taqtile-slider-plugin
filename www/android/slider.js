var exec = require('cordova/exec');

var Slider = function () {

};

Slider.show = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "show", ["test2"])
};

Slider.close = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "close", ["test"])
};


module.exports = Slider;

if (!window.plugins) {
    window.plugins = {};
}
if (!window.TaqtileSlider) {
    window.TaqtileSlider = Slider;
}