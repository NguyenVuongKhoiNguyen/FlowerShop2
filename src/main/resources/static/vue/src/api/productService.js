import api from "./axiosConfig";

export const createProduct = async (formData) => {
    const response = await api.post("/dashboard/products", formData);
    return response;
};

export const updateProduct = async (formData, id) => {
    const response = await api.put(`/dashboard/products/${id}`, formData);
    return response;
};

export const deleteProduct = async (id) => {
    await api.delete(`dashboard/products/${id}`);
};

export const getAllPaginatedAndFilteredProducts = async (params) => {
    const request = await api.get("/products", {params});
    return request;
};

export const getTotalProductPages = async (params) => {
    const request = await api.get("/products/total-pages", {params});
    return request;
}

export const getProductsWithTopSales = async () => {
    const request = await api.get("/products/top-sales");
    return request;
};

export const getRandomProducts = async () => {
    const request = await api.get("/products/random");
    return request;
};

export const getProductById = async (id) => {
    const request = await api.get(`/products/${id}`);
    return request;
}


