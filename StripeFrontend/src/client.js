import axios from "axios";

const apiUrl = "http://localhost:8080/api/v1/";

export const performLogin = async(usernameAndPassword)=>{
    try {
        return axios.post(apiUrl+"login",usernameAndPassword);
    } catch (error) {
        throw error
    }
}

export const signup = async(usernameAndPassword)=>{
    try {
        return axios.post(apiUrl+"signup",usernameAndPassword)
    } catch (error) {
        throw error
    }
}