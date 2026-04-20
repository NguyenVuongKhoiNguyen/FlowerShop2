import api from '../api/axiosConfig';

export const getProductComments = async (productId) => {
    const request = await api.get(`/comments/${productId}`);
    return request;
}

export const getCommentReplies = async (commentId) => {
    const request = await api.get(`/comments/replies/${commentId}`);
    return request;
}

export const createComment = async (comment) => {
    const request = await api.post("/accounts/comment", comment);
    return request;
}

export const createReply = async (reply) => {
    const request = await api.post("/accounts/reply", reply);
    return request;
}

export const deleteComment = async (commentId) => {
    const request = await api.delete(`/accounts/rcomment/${commentId}`);
    return request;
}

export const deleteReply = async (replyId) => {
    const request = await api.delete(`/accounts/reply/${replyId}`);
    return request;
}