var exec = require("cordova/exec");

function PhoneNumber() {}

/**
 * Get device info
 *
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
PhoneNumber.prototype.getInfo = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, "PhoneNumber", "123", []);
};

module.exports = new PhoneNumber();
