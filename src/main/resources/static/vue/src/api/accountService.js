import api from "./axiosConfig";

export const createAccount = async (account) => {
    const response = await api.post("/dashboard/accounts", account);
    return response;
}

export const updateAccount = async (account, username) => {
    const response = await api.put(`/dashboard/accounts/${username}`, account);
    return response;
}

export const deleteAccount = async (username) => {
    return await api.delete(`/dashboard/accounts/${username}`);
}

export const getAccountByUsername = async (username) => {
    const request = await api.get(`/accounts/${username}`);
    return request;
}

export const getAllPaginatedAndFilteredAccounts = async (params) => {
    const request = await api.get("/dashboard/accounts", {params});
    return request;
}

export const getAccountTotalPages = async (params) => {
    const request = await api.get("/dashboard/accounts/total-pages", {params});
    return request;
}

export const updateNonAdminAccount = async (account, username) => {
    const response = await api.put(`/accounts/${username}`, account);
    return response;
}


