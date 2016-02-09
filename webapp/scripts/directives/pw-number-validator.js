'use strict';

/**
 * @ngdoc directive
 * @name panelApp.directive:pwNumberValidator
 * @description
 * # pwNumberValidator
 */
angular.module('panelApp')
  .directive('pwNumber', function() {
    return {
      require: 'ngModel',
      link: function(scope, elem, attrs, ctrl) {
        ctrl.$validators.pwNumber = function(modelValue, viewValue) {
          if (!viewValue) {
            viewValue = 0;
          }
          var numbers = 0;
          for (var i = 0; i < viewValue.length; i++) {
            if ('0' <= viewValue[i] && viewValue[i] <= '9') {
              numbers++;
              if (numbers >= scope.passwordConfig.minNumbers) {
                return true;
              }
            }
          }
          return false;
        };
      }
    };
  });
