import api from "./axiosConfig";

export const createCart = async (cart) => {
    const response = await api.post("/carts", cart);
    return response;
}

export const updateCart = async (cart, cartId) => {
    const response = await api.put(`/carts/${cartId}`, cart);
    return response;
}

export const deleteCartById = async (id) => {
    return api.delete(`/carts/${id}`);
}

export const getCartById = async (id) => {
    const request = await api.get(`/carts/id/${id}`);
    return request;
}

export const getCartsByUsername = async (username) => {
    const request = await api.get(`/carts/user/${username}`);
    return request;
}