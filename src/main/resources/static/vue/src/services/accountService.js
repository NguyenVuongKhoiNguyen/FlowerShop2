import { defineStore } from 'pinia'
import { reactive, ref, watch } from 'vue'
import api from './axiosConfig'

export const useUserStore = defineStore("account", () => {

    const account = reactive({
        username: "",
        password: "",
        fullname: "",
        email: "",
        photo: "",
        phone: "",
        address: "",
        activated: false,
        roles: []
    })

    const filter = reactive({
        username: '',
        fullname: '',
        email: '',
        activated: true,
        sortOrder: 'DESC',
        pageNumber: 0,
        pageSize: 5
    })

    const pages = new Map();
    const size = 5;

    function save(file) {

        const formData = new FormData()

        formData.append('username', account.username)
        formData.append('password', account.password)
        formData.append('fullname', account.fullname)
        formData.append('email', account.email)
        formData.append('phone', account.phone)
        formData.append('address', account.address)
        formData.append('activated', account.activated)

        account.roles.forEach(role => {
            formData.append('roles', role)
        })

        if (file) {
            formData.append('file', file)
        }

        api.post('/dashboard/account', formData)
            .then(res => {
                Object.assign(account, res.data)
                pages.clear()
                preload()
                getPage()
            })
            .catch(err => {
                console.error('Create failed', err)
                throw err
            })
    }

    function remove(username) {
        api.delete(`/dashboard/account/${username}`)
            .then(() => {
                pages.clear()
                preload()
                getPage()
            })
            .catch(err => {
                console.error('Delete failed', err)
                throw err
            })
    }

    function getPage() {

        const key = filter.username + '_'
                    filter.fullname + '_'
                    filter.email + '_'
                    filter.activated + '_'
                    filter.sortOrder + '_'
                    filter.pageNumber + '_'
                    filter.pageSize + '_'
        
        const value = pages.has(key)
        
        if (value) {
            pages.delete(key);
            pages.set(key, value);
            return value;
        }

        if (pages.size >= size) {
            pages.delete(this.pages.keys().next().value);
        }

        api.get("/dashboard/account", { params: filter })
            .then(res => {
                pages.set(key, res.data)
                return value
            })
            .catch(err => {
                console.error("Get page failed", err)
                throw err;
            })
    }

    function preload() {
        
        api.get("/dashboard/account/preload")
            .then(res => {
                pages = new Map(Object.entries(res.data))
                return res.data
            })
            .catch(err => {
                console.error("Preload failed", err)
                throw err
            })
    }

    watch(filter, () => { getPage() }, {deep: true})

    return {
        account,
        filter,
        save,
        remove,
        getPage,
        preload
    }
})