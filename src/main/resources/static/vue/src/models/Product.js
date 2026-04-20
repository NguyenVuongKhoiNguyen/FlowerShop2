export default class Product {
    constructor(name, image, costPrice, retailPercentage, available, amount, categoryId = null) {
        this.name = name;
        this.image = image,
        this.costPrice = costPrice,
        this.retailPercentage = retailPercentage,
        this.available = available,
        this.amount = amount,
        this.categoryId = categoryId
    }
}