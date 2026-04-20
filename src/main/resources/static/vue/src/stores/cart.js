import { defineStore } from "pinia";

export const useCartStore = defineStore(
    "cart",
    {
        state: () => ({
            items: [],
            successfullyAdded: false
        }), //({ something }) means return that confusing shit
        actions: {
            addItem(item) {
                const existedItem = this.items.find(i => i.productId === item.productId)
                if (existedItem) {
                    existedItem.quantity += item.quantity;
                    existedItem.subtotal = existedItem.price * existedItem.quantity; 
                    return;
                }
                this.items.push(item)
            },
            removeItem(id) {
                this.items = this.items.filter(item => item.productId !== id)
            },
            clearCart() {
                this.items = []
            }
        },
        getters: {
            itemCount: (state) => state.items.length,
            totalPrice: (state) => state.items.reduce((sum, item) => sum + item.subtotal, 0)
        },
        persist: {
            storage: localStorage,
            paths: ["items"]
        }
    }
);