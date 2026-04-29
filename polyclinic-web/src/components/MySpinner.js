import { Spinner } from "react-bootstrap";

const MySpinner = () => {
    return(
        <div className="d-flex justify-content-center my-4">
            <Spinner animation="grow" variant="primary" />
        </div>
    )
}

export default MySpinner;