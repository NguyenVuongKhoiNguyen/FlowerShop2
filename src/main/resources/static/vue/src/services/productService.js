import { defineStore } from 'pinia'
import { reactive, ref, watch } from 'vue'
import api from './axiosConfig'

export const useUserStore = defineStore("product", () => {

    const product = reactive({
        id: null,
        name: '',
        image: '',
        costPrice: 0,
        retailPercentage: 0.0,
        createDate: new Date(),
        available: true,
        amount: 0,
        sales: 0,
        categoryId: null
    })

    const filter = reactive({
        minPrice: '',
        maxPrice: '',
        categoryId: '',
        productName: '',
        available: true,
        sortOrder: 'DESC',
        page: 0,
        pageSize: 5
    })

    const pages = new Map()
    const size = 5

    function save(file) {
        
        const formData = new FormData();

        fromData.append(product.id);
        fromData.append(product.name);
        fromData.append(product.image);
        fromData.append(product.costPrice);
        fromData.append(product.retailPercentage);
        fromData.append(product.createDate);
        fromData.append(product.available);
        fromData.append(product.amount);
        fromData.append(product.sales);
        fromData.append(product.categoryId);

        if (file) {
            formData.append('file', file);
        }

        api.post('/dashboard/product', formData)
            .then( res => {
                Object.assign(product, res.data)
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
        api.delete('/dashboard/product', id)
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
        
        const key = filter.minPrice + '_' 
                    + filter.maxPrice + '_' 
                    + filter.categoryId + '_' 
                    + filter.productName + '_' 
                    + filter.available + '_' 
                    + filter.sortOrder + '_' 
                    + filter.page + '_' 
                    + filter.pageSize        

        const value = pages.has(key)

        if (value) {
            pages.delete(key)
            pages.set(key, value)
            return value
        }

        if (pages.size > size) {
            pages.delete(this.pages.keys().next().value)
        }

        api.get('/product', { params: filter })
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