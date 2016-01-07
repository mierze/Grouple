'use strict'
function Messages(Getter) {
    this.get = get;
    
    return {
        get: this.get
    };
    
    function get(id, type, callback) {
        Getter.get('http://groupleapp.herokuapp.com/api/' + type + '/messages/' + params.id, callback);
    }
}

module.exports = Messages;
