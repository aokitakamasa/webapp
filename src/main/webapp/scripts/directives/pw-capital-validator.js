'use strict';

/**
 * @ngdoc directive
 * @name panelApp.directive:pwCapitalValidator
 * @description
 * # pwCapitalValidator
 */
angular.module('panelApp')
  .directive('pwCapital', function() {
    return {
      require: 'ngModel',
      link: function(scope, elem, attrs, ctrl) {
        ctrl.$validators.pwCapital = function(modelValue, viewValue) {
          if (!viewValue) {
            viewValue = 0;
          }
          var capitals = 0;
          for (var i = 0; i < viewValue.length; i++) {
            if ('A' <= viewValue[i] && viewValue[i] <= 'Z') {
              capitals++;
              if (capitals >= scope.passwordConfig.minCapital) {
                return true;
              }
            }
          }
          return false;
        };
      }
    };
  });
