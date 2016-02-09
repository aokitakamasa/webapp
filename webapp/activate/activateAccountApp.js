'use strict';

angular
  .module('activateAccountApp', ['ngAnimate', 'ngMessages'])
  .config(function($httpProvider) {
    // Configure all post request as urlencoded
    $httpProvider.defaults.headers.post = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };
    $httpProvider.defaults.headers.put = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };
    $httpProvider.defaults.transformRequest = function(obj) {
      var str = [];
      for (var p in obj) {
        str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
      }
      return str.join('&');
    };
  })
  .controller('ActivateAccountCtrl', function($scope, $timeout, activateAccountService, validateTokenService, activationConfirmedService) {

    $scope.loading = true;
    $scope.tokenValidStatus = '';
    $scope.token = null;
    $scope.tokenCheck = tokenCheck;
    $scope.activateAcountFormSubmit = activateAcountFormSubmit;
    $scope.resetForm = resetForm;
    $scope.activateSuccess = false;
    $scope.passwordConfig = {};

    var userData = {
      newPassword: null,
      confirmPassword: null
    };

    function tokenCheck() {
      var tokenStr = window.location.search;
      var tokenCorrect = /\?token=\w/.test(tokenStr);

      if (tokenCorrect) {
        $scope.tokenValidStatus = 'correct';
        $scope.token = tokenStr.slice(7);

        var tokenObj = {
          token: $scope.token
        };

        validateTokenService(tokenObj).then(function success(resp) {
          if (resp.status === 200 && resp.data) {
            $scope.passwordConfig = resp.data;
            $scope.tokenValidStatus = 'valid';
            $scope.loading = false;
            console.log($scope.passwordConfig);
          }
          console.log(resp);
        }, function error(resp) {
          console.log(resp);
          $scope.tokenValidStatus = 'invalid';
          $scope.loading = false;

          // $timeout(function() {
          //   window.location.replace('/panel/login.html');
          // }, 5000);
        });
      } else {
        $scope.tokenValidStatus = 'incorrect';
        $scope.loading = false;
      }
    }

    function activateAcountFormSubmit(userData) {
      var passwordObj = {
        token: $scope.token,
        password: userData.confirmPassword
      };

      console.log(passwordObj);

      activateAccountService(passwordObj).then(function success(resp) {
        if (resp.status === 200) {
          $scope.activateSuccess = true;
          // Wysyłanie maila o udanej aktywacji konta
          activationConfirmedService(passwordObj).then(function success(resp2) {
            console.log(resp2);
            $timeout(function() {
              window.location.replace('/panel/index.html');
            }, 3000);
          }, function error(resp2) {
            console.log(resp2);
            $timeout(function() {
              window.location.replace('/panel/index.html');
            }, 3000);
          });
        }
      }, function error(resp) {
        console.log(resp);
      });
    }

    function resetForm(form) {
      $scope.userData = angular.copy(userData);
      form.$setPristine();
    }
  })
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
  })
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
  })
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
  })
  .directive('pwSpecial', function() {
    return {
      require: 'ngModel',
      link: function(scope, elem, attrs, ctrl) {
        ctrl.$validators.pwSpecial = function(modelValue, viewValue) {
          if (!viewValue) {
            viewValue = 0;
          }
          var specialsCollection = '!@#$%^&*:\'<>/?';
          var specials = 0;
          for (var i = 0; i < viewValue.length; i++) {
            for (var j = 0; j < specialsCollection.length; j++) {
              if (viewValue[i] === specialsCollection[j]) {
                specials++;
              }
            }
            if (specials >= scope.passwordConfig.minSpecial) {
              return true;
            }
          }
          return false;
        };
      }
    };
  })
  .directive('pwMatch', function() {
    return {
      require: 'ngModel',
      link: function(scope, elem, attrs, ctrl) {
        ctrl.$validators.pwMatch = function(modelValue, viewValue) {
          var p1 = scope.activateAccountForm.newPassword.$viewValue;
          var p2 = scope.activateAccountForm.confirmPassword.$viewValue;
          if (scope.activateAccountForm.confirmPassword.$dirty) {
            if (p1 === p2) {
              scope.activateAccountForm.newPassword.$setValidity('pwMatch', true);
              scope.activateAccountForm.confirmPassword.$setValidity('pwMatch', true);
              return true;
            }
            return false;
          }
          return true;
        };
      }
    };
  })
  .service('validateTokenService', function($http) {
    return function(tokenObj) {
      return $http.post('/panel/api/user/validateToken', tokenObj);
    };
  })
  .service('activateAccountService', function($http) {

    $http.post('/panel/api/logout').then(function success(resp) {
      console.log(resp);
    }, function error(resp) {
      console.log(resp);
    });

    return function(userData) {
      return $http.post('/panel/api/user/consumeToken', userData);
    };
  })
  .service('activationConfirmedService', function($http) {
    return function(passwordObj) {
      return $http.post('/panel/api/user/activationConfirmed', passwordObj);
    };
  });
