module.exports = angular.module('app.services', [])
    .factory('Helpers', function () {
        return {
            getPromise: function (deferred) {
                var promise = deferred.promise;
                promise.success = function (fn) {
                    promise.then(fn);
                    return promise;
                };
                promise.error = function (fn) {
                    promise.then(null, fn);
                    return promise;
                };
                return promise
            }
        }
    });

require("./services/**/*.js", {mode: 'expand'});
