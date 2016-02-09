'use strict';

/**
 * @ngdoc directive
 * @name panelApp.directive:pwMatchValidator
 * @description
 * # pwMatchValidator
 */
angular.module('panelApp')
  .directive('pwMatch', function() {
    return {
      require: 'ngModel',
      link: function(scope, elem, attrs, ctrl) {
        ctrl.$validators.pwMatch = function(modelValue, viewValue) {
          var formName = attrs.formName;
          var p1 = scope[formName].newPassword.$viewValue;
          var p2 = scope[formName].confirmPassword.$viewValue;
          if (scope[formName].confirmPassword.$dirty) {
            if (p1 === p2) {
              scope[formName].newPassword.$setValidity('pwMatch', true);
              scope[formName].confirmPassword.$setValidity('pwMatch', true);
              return true;
            }
            return false;
          }
          return true;
        };
      }
    };
  });
