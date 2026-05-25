import { defineStore } from 'pinia'
import { reactive, ref, watch } from 'vue'
import api from './axiosConfig'

export const useUserStore = defineStore("product", () => {

    const product = reactive({
        id: null,
        name: '',
        image: '',
        costPrice: 0,
        rentalPercentage: 0.0,
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


})