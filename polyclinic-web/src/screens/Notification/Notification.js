import { useContext, useEffect, useState } from "react";
import { MyNotificationContext, MyUserContext } from "../../configs/Contexts";
import { authApis, endpoints } from "../../configs/Api";
import Header from "../../components/Header";
import { Badge, Button, Pagination, Spinner } from "react-bootstrap";
import Moment from "react-moment";
import Footer from "../../components/Footer";
import MySpinner from "../../components/MySpinner";

const PAGE_SIZE = 5;

const Notification = () => {

    const [notificationState, notificationDispatch] = useContext(MyNotificationContext);
    const { notifications, loading, error } = notificationState;
    const [markingAll, setMarkingAll] = useState(false);
    const [page, setPage] = useState(1);

    const unreadCount = notifications.filter(n => !n.isRead).length;
    const totalPages = Math.ceil(notifications.length / PAGE_SIZE);
    const visibleNotifications = notifications.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);

    const markAsRead = async (id) => {
        try {
            await authApis().get(endpoints['read-notifications'](id));
            notificationDispatch({ type: "MARK_READ", payload: id });
        }
        catch (err) {
            notificationDispatch({ type: "SET_ERROR", payload: "Lỗi! Đọc thông báo thất bại" });
        }
    }


    const markAllAsRead = async () => {
        const unread = notifications.filter(n => !n.isRead);
        if (unread.length === 0) return;
        try {
            setMarkingAll(true);
            await Promise.all(
                unread.map(n => authApis().get(endpoints['read-notifications'](n.id)))
            );
            notificationDispatch({ type: "MARK_ALL_READ" });
        }
        catch (err) {
            notificationDispatch({ type: "SET_ERROR", payload: "Lỗi! Đọc tất cả thông báo thất bại" });
        }
        finally {
            setMarkingAll(false);
        }

    };

    const getIconForTitle = (title) => {
        if (!title) return "bi-bell-fill";
        if (title.includes("lịch") || title.includes("Lịch") || title.includes("hẹn"))
            return "bi-calendar-check-fill";
        if (title.includes("Thanh toán") || title.includes("thanh toán"))
            return "bi-credit-card-fill";
        if (title.includes("tái khám") || title.includes("Tái khám"))
            return "bi-arrow-repeat";
        if (title.includes("Cảnh báo") || title.includes("cảnh báo"))
            return "bi-exclamation-triangle-fill";
        return "bi-bell-fill";
    };

    const getColorForTitle = (title) => {
        if (!title) return "#6c757d";
        if (title.includes("lịch") || title.includes("Lịch") || title.includes("hẹn"))
            return "var(--primary, #0d6efd)";
        if (title.includes("Thanh toán") || title.includes("thanh toán"))
            return "#198754";
        if (title.includes("tái khám") || title.includes("Tái khám"))
            return "#fd7e14";
        if (title.includes("Cảnh báo") || title.includes("cảnh báo"))
            return "#dc3545";
        return "#6c757d";
    };




    return (
        <>
            <Header />
            <div className="container py-4 noti-page">
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <div className="d-flex align-items-center gap-3">
                        <h4 className="mb-0 fw-bold">
                            Thông báo
                        </h4>
                        {unreadCount > 0 && (
                            <Badge bg="danger" pill>{unreadCount} chưa đọc</Badge>
                        )}
                    </div>
                    {unreadCount > 0 && (
                        <Button
                            variant="outline-primary"
                            size="sm"
                            onClick={markAllAsRead}
                            disabled={markingAll}
                        >
                            {markingAll ?
                                <><Spinner animation="border" size="sm" className="me-1" />Đang xử lý</>
                                : <><i className="bi bi-check2-all me-1"></i>Đánh dấu tất cả đã đọc</>
                            }
                        </Button>
                    )}
                </div>



                {error && (
                    <div className="alert alert-danger d-flex align-items-center gap-2" role="alert">
                        <i className="bi bi-exclamation-triangle-fill"></i>
                        {error}
                    </div>
                )}

                {loading ? (
                    <div className="text-center py-5">
                        <MySpinner />
                        <p className="mt-2 text-muted">Đang tải thông báo...</p>
                    </div>

                ) : notifications.length === 0 ? (
                    <div className="text-center py-5 text-muted">
                        <p className="mt-3">Bạn chưa có thông báo nào.</p>
                    </div>
                ) : (
                    <div className="d-flex flex-column gap-2">
                        {visibleNotifications.map(n => (
                            <div key={n.id}
                                onClick={() => !n.isRead && markAsRead(n.id)}
                                className={`noti-item ${n.isRead ? "noti-item--read" : "noti-item--unread"}`}
                            >

                                <div className="d-flex align-items-start gap-3 mb-3">
                                    <div className={`noti-icon-wrap ${n.isRead ? "noti-icon-wrap--read" : "noti-icon-wrap--unread"}`}>
                                        <i
                                            className={`bi ${getIconForTitle(n.title)} noti-icon`}
                                            style={{ color: getColorForTitle(n.title) }}
                                        ></i>
                                    </div>
                                    <div className="flex-grow-1">
                                        <div className="d-flex justify-content-between align-items-start">
                                            <span className={`fw-semibold ${n.isRead ? "text-secondary" : "text-dark"}`}>
                                                {n.title}
                                            </span>
                                            <div className="d-flex align-items-center gap-2 ms-2 flex-shrink-0">
                                                <small className="text-muted">{<Moment fromNow>{n.ngayTao}</Moment>}</small>
                                                {!n.isRead && (
                                                    <span className="noti-dot"></span>
                                                )}
                                            </div>
                                        </div>
                                        <p className={`mb-0 mt-1 small ${n.isRead ? "text-muted" : "text-dark"}`}>{n.message}</p>
                                        {!n.isRead && (
                                            <small className="text-primary mt-1 d-inline-block">
                                                <i className="bi bi-cursor me-1"></i>Nhấn để đánh dấu đã đọc
                                            </small>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {totalPages > 1 && (
                    <div className="d-flex justify-content-center mt-5">
                        <Pagination>
                            <Pagination.Prev disabled={page === 1} onClick={() => setPage(page - 1)} />
                            {[...Array(totalPages)].map((p, i) => (
                                <Pagination.Item
                                    key={i + 1}
                                    active={page === i + 1}
                                    onClick={() => setPage(i + 1)}
                                >
                                    {i + 1}
                                </Pagination.Item>
                            ))}
                            <Pagination.Next disabled={page === totalPages} onClick={() => setPage(page + 1)} />
                        </Pagination>
                    </div>
                )}
            </div>
            <Footer />
        </>
    );
};

export default Notification;