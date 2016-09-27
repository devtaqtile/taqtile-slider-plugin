var exec = require('cordova/exec');

var Slider = function () {

};

Slider.show = function (items, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "show", [items])
};

function setBackground(element) {
    if(!element) return false;

    element.style.background = "transparent";
    setBackground(element.parentElement)
}

Slider.init = function (element, successCallback, errorCallback) {
    var elementParams = {
        "width": element.getBoundingClientRect().width,
        "height": element.getBoundingClientRect().height,
        "top": element.getBoundingClientRect().top
    };

    setBackground(element);

    exec(successCallback, errorCallback, "SliderPlugin", "init", [elementParams])
};

Slider.close = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "close", [])
};

Slider.destroy = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "destroy", [])
};

Slider.setclicable = function (value, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "SliderPlugin", "setClickable", [value])
};


module.exports = Slider;

if (!window.plugins) {
    window.plugins = {};
}
if (!window.TaqtileSlider) {
    window.TaqtileSlider = Slider;
}