'use strict';

/**
 * @ngdoc directive
 * @name panelApp.directive:pwLengthValidator
 * @description
 * # pwLengthValidator
 */
angular.module('panelApp')
  .directive('pwLength', function() {
    return {
      require: 'ngModel',
      link: function(scope, elem, attrs, ctrl) {
        ctrl.$validators.pwLength = function(modelValue, viewValue) {
          if (!viewValue) {
            viewValue = 0;
          }
          if (scope.passwordConfig && viewValue.length >= scope.passwordConfig.minLength) {
            return true;
          }
          return false;
        };
      }
    };
  });
