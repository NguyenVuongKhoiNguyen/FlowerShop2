import { defineStore } from 'pinia'
import { reactive, ref, watch } from 'vue'
import api from './axiosConfig'

export const useUserStore = defineStore("category", () => {

    const category = reactive({
        id: '',
        name: ''
    })

    const filter = reactive({
        keyword: '',
        sortOrder: 'DESC',
        page: 1,
        pageSize: 5
    })

    const pages = new Map()
    const size = 5

    function save(file) {

        api.post('/dashboard/category', category)
            .then( res => {
                Object.assign(category, res.data)
                pages.clear()
                preload()
                getPage()
            })
            .catch(err => {
                console.log('Create fail', err)
                throw err
            })
    }

    function remove(id) {
        api.delete('/dashboard/category', id)
            .then(res => {
                pages.clear()
                preload()
                getPage()
            })
            .catch(err => {
                console.log("Remove fail", err)
                throw err
            })
    }

    function getPage() {
        
        const key = filter.keyword + '_' 
                    + filter.sortOrder + '_'
                    + filter.page + '_'
                    + filter.pageSize + '_'

        const value = pages.has(key)

        if (value) {
            pages.delete(key)
            pages.set(key, value)
            return value
        }

        if (pages.size > size) {
            pages.delete(this.pages.keys().next().value)
        }

        api.get('/category', { params: filter })
            .then(res => {
                pages.set(key, res.data)
                return res.data
            })
            .catch(err => {
                console.log('Get page fail', err)
                throw err
            })
    }

    function preload() {

        api.get('/product/preload')
            .then(res => {
                pages = new Map(Object.entries(res.data))
            })
            .catch(err => {
                console.log('Preload fail', err)
                throw err
            })
    }

    
})