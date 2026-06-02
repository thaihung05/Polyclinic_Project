const MyNotificationReducer = (current, action) => {
    switch (action.type) {
        case "LOAD":
            return { ...current, notifications: action.payload, loading: false, error: '' };
        case "SET_LOADING":
            return { ...current, loading: action.payload };
        case "SET_ERROR":
            return { ...current, error: action.payload, loading: false };
        case "MARK_READ":
            return {
                ...current, notifications: current.notifications.map(
                    n => n.id === action.payload ? { ...n, isRead: true } : n
                )
            };
        case "MARK_ALL_READ":
            return { ...current, notifications: current.notifications.map(n => ({ ...n, isRead: true })) };
        case "CLEAR":
            return {notifications: [], loading: false, error: ''};
        default:
            return current;
    }
}

export default MyNotificationReducer;