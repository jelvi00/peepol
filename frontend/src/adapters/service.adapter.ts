import { LoggerService } from "@/services";
import axios, { AxiosError, RawAxiosHeaders } from "axios";

export abstract class ServiceAdapter {

    private readonly adapterConfig;

    protected constructor(config: { baseUrl: string }) {

        if (!config.baseUrl) throw new Error("Base URL is required");

        this.adapterConfig = {
            baseUrl: config.baseUrl
        };

    }

    async makeHTTPRequest(
        method: string,
        endpoint: string,
        data?: Record<string, unknown>,
        headers?: RawAxiosHeaders
    ) {

        const url = this.adapterConfig.baseUrl + endpoint;

        try {

            const response = await axios({ method, url, data, headers });

            return {
                status: response.status,
                body: response.data
            };

        } catch (error) {

            const axiosError = error as AxiosError;

            const errorResponse = axiosError?.response;

            LoggerService.log("\nERROR:", axiosError);

            return {
                status: errorResponse?.status || 500,
                body: errorResponse?.data || { message: axiosError.message }
            };
        }
    }

}
