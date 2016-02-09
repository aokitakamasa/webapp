'use strict';

/**
 * @ngdoc service
 * @name panelApp.authService
 * @description
 * # authService
 * Service in the panelApp.
 */
angular.module('panelApp')
  .service('authService', function($http, $q) {

    function login(userData) {
      var deferred = $q.defer();

      $http.get('/panel/j_security_check', userData)
        .then(function success(resp) {
          if (resp === 'success') {
            console.log(resp);
            deferred.resolve();
          } else {
            console.log(resp);
            deferred.reject();
          }
        }, function error(resp) {
          console.log(resp.data);
        });

      return deferred.promise;
    }

    function logout() {
      if (localStorage.getItem('ttl')) {
        localStorage.removeItem('ttl');
      }
      $http.post('/api/logout').then(function success(resp) {
        console.log(resp);
        window.location.replace('/panel/login.html');

      }, function error(resp) {
        console.log(resp);
      });

      // $rootScope.$broadcast('logout', {
      //   logoutMsg: 'You are successfully logged out!'
      // });
    }

    return {
      login: login,
      logout: logout
    };

  });
