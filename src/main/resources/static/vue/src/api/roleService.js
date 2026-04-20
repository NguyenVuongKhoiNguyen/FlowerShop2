import api from './axiosConfig';

export const getAllRoles = () => {
    const request = api.get("/dashboard/roles");
    return request;
}
