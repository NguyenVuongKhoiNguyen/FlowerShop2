import OrderDetail from "./OrderDetail";

export default class Order {
    constructor(username, fullname, phone, address, status, orderDetails = []) {
        this.username = username;
        this.fullname = fullname;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    addOrderDetail(productId, quantity) {
        this.orderDetails.push(new OrderDetail(productId, quantity));
    }
}