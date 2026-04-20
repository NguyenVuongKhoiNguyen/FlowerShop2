import api from "./axiosConfig";

export const createCategory = async (category) => {
    return await api.post("/dashboard/categories", category);
};

export const updateCategory = async (id, category) => {
    return await api.put(`/dashboard/categories/${id}`, category);
};

export const deleteCategory = async (id) => {
    return await api.delete(`/dashboard/categories/${id}`);
};

export const getCategoryById = async (id) => {
    const request = await api.get(`/categories/${id}`);
    return request;
}

export const getAllPaginatedAndFilteredCategories = async (params) => {
    const request = await api.get("/categories", {params});
    return request;
};