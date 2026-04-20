import Item from "./Item";

export default class Cart {
    constructor(username, items = []) {
        this.username = username;
        this.items = items;
    }

    addItem(productId, quantity) {
        this.items.push(new Item(productId, quantity));
    }
}