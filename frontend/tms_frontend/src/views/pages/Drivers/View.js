import React from "react";
import {
    Card,
    CardHeader,
    CardTitle,
    CardBody,
    Media,
    Row,
    Col,
    Button,
    CustomInput,
} from "reactstrap";
import { Edit } from "react-feather";
import { Link } from "react-router-dom";
import { connect } from "react-redux";
import { toast, Flip } from "react-toastify";
import "../../../assets/scss/pages/users.scss";
class View extends React.Component {
    state = {
        driverStatuses: [],
        paymentTypes: [],
        status: null,
        data: {},
    };
    setStatus = (id) => {
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/driver/edit-status/${this.props.match.params.id}/${id}`,
            {
                headers: {
                    Authorization: this.props.token,
                },
                method: "PUT",
            }
        ).then((res) => {
            if (res.ok) {
                toast.success("Status successfuly changed", {
                    transition: Flip,
                });
                this.setState({
                    status: id,
                });
            } else {
                toast.error("Something went wrong", { transition: Flip });
                res.text();
            }
        });
    };
    componentDidMount() {
        fetch(process.env.REACT_APP_BASE_URL + "/driver/context", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    driverStatuses: data.driver_statuses,
                    paymentTypes: data.payment_types,
                });
                fetch(
                    process.env.REACT_APP_BASE_URL +
                        `/driver/${this.props.match.params.id}`,
                    {
                        headers: {
                            Authorization: this.props.token,
                        },
                    }
                )
                    .then((res) => res.json())
                    .then((data) => {
                        this.setState({ data: data });
                        this.setState({
                            status: data.driverStatusId,
                        });
                    });
            });
    }
    render() {
        let { data } = this.state;
        return (
            <React.Fragment>
                <Row>
                    <Col sm="12">
                        <Card>
                            <CardHeader>
                                <CardTitle>Driver</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <Row className="mx-0" col="12">
                                    <Col className="pl-0" sm="12">
                                        <Media className="d-sm-flex d-block">
                                            <Media body>
                                                <Row>
                                                    <Col sm="9" md="6" lg="6">
                                                        <div className="users-page-view-table">
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Name
                                                                </div>
                                                                <div>
                                                                    {data.firstName +
                                                                        " " +
                                                                        data.lastName}
                                                                </div>
                                                            </div>
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Email
                                                                </div>
                                                                <div className="text-truncate">
                                                                    <span>
                                                                        {
                                                                            data.email
                                                                        }
                                                                    </span>
                                                                </div>
                                                            </div>
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Phone number
                                                                </div>
                                                                <div>
                                                                    {data.phone}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </Col>
                                                    <Col md="12" lg="6">
                                                        <div className="users-page-view-table">
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    City
                                                                </div>
                                                                <div>
                                                                    {data.city}
                                                                </div>
                                                            </div>
                                                            <div className="d-flex user-info">
                                                                <div className="user-info-title font-weight-bold">
                                                                    Street
                                                                </div>
                                                                <div>
                                                                    <span>
                                                                        {
                                                                            data.street
                                                                        }
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </Col>
                                                </Row>
                                            </Media>
                                        </Media>
                                    </Col>
                                    <Col className="mt-1 pl-0 d-flex" sm="12">
                                        {this.props.userRole !==
                                            "dispatcher" && (
                                            <Link
                                                to={`/driver/edit/${this.props.match.params.id}`}
                                            >
                                                <Button
                                                    className="mr-1"
                                                    color="primary"
                                                    type="button"
                                                    outline
                                                >
                                                    <Edit size={15} />
                                                    <span className="align-middle ml-50">
                                                        Edit
                                                    </span>
                                                </Button>
                                            </Link>
                                        )}

                                        {this.props.userRole !==
                                            "dispatcher" && (
                                            <CustomInput
                                                style={{
                                                    width: 230,
                                                    height: 41,
                                                }}
                                                type="select"
                                                name="select"
                                                id="driver_status"
                                                onChange={(e) =>
                                                    this.setStatus(
                                                        e.target.value
                                                    )
                                                }
                                                value={this.state.status}
                                            >
                                                {this.state.driverStatuses.map(
                                                    (item) => (
                                                        <option
                                                            key={item.id}
                                                            value={item.id}
                                                        >
                                                            {item.name}
                                                        </option>
                                                    )
                                                )}
                                            </CustomInput>
                                        )}
                                    </Col>
                                </Row>
                            </CardBody>
                        </Card>
                    </Col>
                    <Col sm="12" md="6">
                        <Card>
                            <CardHeader>
                                <CardTitle>Information</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <div className="users-page-view-table">
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Alternate Phone Number
                                        </div>
                                        <div>{data.alternatePhone}</div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Hire Date
                                        </div>
                                        <div className="text-truncate">
                                            <span>
                                                {data.hireDateFormatted}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            License Number
                                        </div>
                                        <div className="text-truncate">
                                            <span>{data.licenseNumber}</span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Medical Card Renewal
                                        </div>
                                        <div className="text-truncate">
                                            <span>
                                                {
                                                    data.medicalCardRenewalFormatted
                                                }
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </CardBody>
                        </Card>
                    </Col>
                    <Col sm="12" md="6">
                        <Card>
                            <CardHeader>
                                <CardTitle>Files</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <div className="users-page-view-table">
                                    <div className="d-flex user-info">
                                        {data.licenseFileId && (
                                            <Button.Ripple
                                                style={{ width: 225 }}
                                                className="d-flex align-items-center"
                                                type="button"
                                                href={`${window.location.origin}/file/${data.licenseFileId}`}
                                                onclick={() =>
                                                    window.open(
                                                        `${window.location.origin}/file/${data.licenseFileId}`,
                                                        "_blank"
                                                    )
                                                }
                                            >
                                                License
                                            </Button.Ripple>
                                        )}
                                        {data.medicalCardFileId && (
                                            <Button.Ripple
                                                style={{ width: 225 }}
                                                className="d-flex align-items-center"
                                                type="button"
                                                href={`${window.location.origin}/file/${data.medicalCardFileId}`}
                                                onclick={() =>
                                                    window.open(
                                                        `${window.location.origin}/file/${data.medicalCardFileId}`,
                                                        "_blank"
                                                    )
                                                }
                                            >
                                                Medical Cart
                                            </Button.Ripple>
                                        )}
                                        {data.socialSecurityFileId && (
                                            <Button.Ripple
                                                style={{ width: 225 }}
                                                className="d-flex align-items-center"
                                                type="button"
                                                href={`${window.location.origin}/file/${data.socialSecurityFileId}`}
                                                onclick={() =>
                                                    window.open(
                                                        `${window.location.origin}/file/${data.socialSecurityFileId}`,
                                                        "_blank"
                                                    )
                                                }
                                            >
                                                Social Security Number
                                            </Button.Ripple>
                                        )}
                                    </div>
                                </div>
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
                <Row>
                    <Col sm="12" md="6">
                        <Card>
                            <CardHeader>
                                <CardTitle>Files</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <div className="users-page-view-table">
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Default Payment Type
                                        </div>
                                        <div className="text-truncate">
                                            <span>
                                                {data.defaultPaymentTypeId &&
                                                    this.state.paymentTypes.find(
                                                        (item) =>
                                                            item.id ==
                                                            data.defaultPaymentTypeId
                                                    ).name}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            License Expiration
                                        </div>
                                        <div className="text-truncate">
                                            <span>28 January 2021</span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Medical Card Expiration
                                        </div>
                                        <div className="text-truncate">
                                            <span>31 December 2021</span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Hire Date
                                        </div>
                                        <div className="text-truncate">
                                            <span>1 January 2021</span>
                                        </div>
                                    </div>
                                    <div className="d-flex user-info">
                                        <div className="user-info-title font-weight-bold">
                                            Termination Date
                                        </div>
                                        <div className="text-truncate">
                                            <span>5 January 2021</span>
                                        </div>
                                    </div>
                                </div>
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
            </React.Fragment>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth.login.token,
        userRole: state.auth.login.userRole,
    };
};
export default connect(mapStateToProps)(View);
