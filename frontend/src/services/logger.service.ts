
class _LoggerService {

    log(message: string, object: unknown = null) {
        try {
            console.log(message, JSON.stringify(object, null, 2))
        } catch (e) {
            this.error(message, object);
        }
    }

    warn(message: string, object: unknown = null) {
        try {
            console.warn(message, JSON.stringify(object, null, 2))
        } catch (e) {
            this.error(message, object);
        }
    }

    error(message: string, object: unknown = null) {
        try {
            console.error(message, JSON.stringify(object, null, 2))
        } catch (e) {
            console.error(message, object)
        }
    }

}

export const LoggerService = new _LoggerService();
