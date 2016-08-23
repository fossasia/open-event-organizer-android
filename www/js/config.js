var config = {
    'APP_CONFIG': {
        'API_ENDPOINT': 'https://open-event-dev.herokuapp.com/api/v2',
        'WHITELISTED_DOMAINS': ['open-event-dev.herokuapp.com']
    }
};

config_module = angular.module('app.config', []);

angular.forEach(config, function(key,value) {
    config_module.constant(value,key);
});

module.exports = config_module;
