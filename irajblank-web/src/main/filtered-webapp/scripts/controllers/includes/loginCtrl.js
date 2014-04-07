'use strict';

angular.module('blank-iraj')
.controller('LoginCtrl', function ($scope, $rootScope) {
	$scope.tryLogin = function(login, password) {
		var digestedPassword = CryptoJS.SHA1(login+'|'+password).toString(CryptoJS.enc.Hex);
		$rootScope.$broadcast('raaj:auth-tryLogin' , login, digestedPassword);
	};
	$rootScope.$on('raaj:auth-loginRequired', function() {
		$('#loginModal').modal('show');
		$('#loginModal').on('shown', function() {
			$('#loginModal-login').focus();
		});
	});
	$rootScope.$on('raaj:auth-loginSucced', function() {
		$('#loginModal').modal('hide');
	});
});