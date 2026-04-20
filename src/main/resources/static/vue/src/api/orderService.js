import api from "./axiosConfig";

export const createOrder = async (order) => {
    const response = await api.post("/orders", order);
    return response;
}

export const updateOrder = async (status, id) => {
    const response = await api.put(`/orders/${id}`, status);
    return response;
}

export const deleteOrder = async (id) => {
    await api.delete(`/orders/${id}`);
}

export const getAllPaginatedAndFilteredOrders = async (params) => {
    const request = await api.get("/orders", {params}); // or {params: parameters}
    return request;
}

export const getTotalPages = async (param) => {
    const request = await api.get("/orders", {param});
    return request;
}
