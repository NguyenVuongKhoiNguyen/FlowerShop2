import Role from './Role'

export default class Account {
    constructor(username, password, fullname, email, photo, phone,address, activated, roles = []) {
        this.username = username,
        this.password = password,
        this.fullname = fullname,
        this.email = email,
        this.photo = photo,
        this.phone = phone,
        this.address = address,
        this.activated = activated,
        this.roles = roles
    }

    addRole(id, name, fullname) {
        this.roles.push(new Role(id, name, fullname));
    }
}