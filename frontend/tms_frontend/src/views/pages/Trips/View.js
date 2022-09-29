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
} from "reactstrap";
import { Link } from "react-router-dom";
import "../../../assets/scss/pages/users.scss";
import Steps, { Step } from "rc-steps";
import "rc-steps/assets/index.css";
import "../../../assets/less/iconfont.css";

class View extends React.Component {
    state = {
        data: {},
    };

    componentDidMount() {
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/trip/${this.props.match.params.id}`,
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.json())
            .then((data) => {
                this.setState({ data });
            });
    }
    render() {
        return (
            <>
                <Card>
                    <CardHeader>
                        <CardTitle>Trip steps</CardTitle>
                    </CardHeader>
                    <CardBody>
                        <Steps current={0}>
                            {this.state.data.chronologicalSequence &&
                                this.state.data.chronologicalSequence.map(
                                    (item) => {
                                        return (
                                            <Step
                                                title={
                                                    item.pickupDateFormatted
                                                        ? item.pickupDateFormatted
                                                        : item.deliveryDateFormatted
                                                }
                                                description={
                                                    item.consigneeNameAndLocation
                                                }
                                            />
                                        );
                                    }
                                )}
                        </Steps>
                    </CardBody>
                </Card>
                <Card>
                    <CardHeader>
                        <CardTitle>
                            Trip number {this.props.match.params.id}
                        </CardTitle>
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
                                                            Customer Name
                                                        </div>
                                                        <div>
                                                            {
                                                                this.state.data
                                                                    .customerName
                                                            }
                                                        </div>
                                                    </div>
                                                    <div className="d-flex user-info">
                                                        <div className="user-info-title font-weight-bold">
                                                            Drivers
                                                        </div>
                                                        <div className="text-truncate">
                                                            <span>
                                                                <Link
                                                                    to={`/driver/view/${this.state.data.driverId}`}
                                                                >
                                                                    {
                                                                        this
                                                                            .state
                                                                            .data
                                                                            .driverName
                                                                    }
                                                                </Link>
                                                                {this.state.data
                                                                    .teammateName && (
                                                                    <>
                                                                        <br />
                                                                        <Link
                                                                            to={`/driver/view/${this.state.data.teammateId}`}
                                                                        >
                                                                            {
                                                                                this
                                                                                    .state
                                                                                    .data
                                                                                    .teammateName
                                                                            }
                                                                        </Link>
                                                                    </>
                                                                )}
                                                            </span>
                                                        </div>
                                                    </div>
                                                    <div className="d-flex user-info">
                                                        <div className="user-info-title font-weight-bold">
                                                            Unit Number
                                                        </div>
                                                        <div>
                                                            <Link
                                                                to={`/unit/edit/${this.state.data.unitNumber}`}
                                                            >
                                                                {
                                                                    this.state
                                                                        .data
                                                                        .unitNumber
                                                                }
                                                            </Link>
                                                        </div>
                                                    </div>
                                                    <div className="d-flex user-info">
                                                        <div className="user-info-title font-weight-bold">
                                                            Company
                                                        </div>
                                                        <div>
                                                            {
                                                                this.state.data
                                                                    .ownedCompanyName
                                                            }
                                                        </div>
                                                    </div>
                                                    <div className="d-flex user-info">
                                                        <div className="user-info-title font-weight-bold">
                                                            Driver <br />{" "}
                                                            Instructions
                                                        </div>
                                                        <div>
                                                            {
                                                                this.state.data
                                                                    .driverInstructions
                                                            }
                                                        </div>
                                                    </div>
                                                </div>
                                            </Col>
                                            <Col md="12" lg="6">
                                                <div className="users-page-view-table">
                                                    <div className="d-flex user-info">
                                                        <div className="user-info-title font-weight-bold">
                                                            Unit Status
                                                        </div>
                                                        <div>
                                                            {
                                                                this.state.data
                                                                    .unitStatusName
                                                            }
                                                        </div>
                                                    </div>
                                                    <div className="d-flex user-info">
                                                        <div className="user-info-title font-weight-bold">
                                                            Driver Status
                                                        </div>
                                                        <div>
                                                            {
                                                                this.state.data
                                                                    .driverStatusName
                                                            }
                                                        </div>
                                                    </div>
                                                    <div className="d-flex user-info">
                                                        <div className="user-info-title font-weight-bold">
                                                            Accessory Pay
                                                        </div>
                                                        <div>
                                                            <span>
                                                                {
                                                                    this.state
                                                                        .data
                                                                        .accessoryDriverPay
                                                                }
                                                            </span>
                                                        </div>
                                                    </div>
                                                    <div className="d-flex user-info">
                                                        <div className="user-info-title font-weight-bold">
                                                            From
                                                        </div>
                                                        <div>
                                                            <span>
                                                                {this.state.data
                                                                    .chronologicalSequence &&
                                                                    this.state
                                                                        .data
                                                                        .chronologicalSequence[0] &&
                                                                    this.state
                                                                        .data
                                                                        .chronologicalSequence[0]
                                                                        .consigneeNameAndLocation}
                                                            </span>
                                                        </div>
                                                    </div>
                                                    <div className="d-flex user-info">
                                                        <div className="user-info-title font-weight-bold">
                                                            To
                                                        </div>
                                                        <div>
                                                            <span>
                                                                {this.state.data
                                                                    .chronologicalSequence &&
                                                                    this.state
                                                                        .data
                                                                        .chronologicalSequence[0] &&
                                                                    this.state
                                                                        .data
                                                                        .chronologicalSequence[
                                                                        this
                                                                            .state
                                                                            .data
                                                                            .chronologicalSequence
                                                                            .length -
                                                                            1
                                                                    ]
                                                                        .consigneeNameAndLocation}
                                                            </span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </Col>
                                        </Row>
                                    </Media>
                                </Media>
                            </Col>
                            {/* <Col className="mt-1 pl-0" sm="12">
                <Button.Ripple className="mr-1" color="primary" outline>
                  <Link to={`/driver/edit/${this.props.match.params.id}`}>
                    <Edit size={15} />
                    <span className="align-middle ml-50">Edit</span>
                  </Link>
                </Button.Ripple>
                <Button.Ripple color="danger" outline>
                  <Trash size={15} />
                  <span className="align-middle ml-50">Delete</span>
                </Button.Ripple>
              </Col> */}
                        </Row>
                        <Link
                            to={`/trips/edit/${this.props.match.params.id}`}
                            className="d-flex align-items-center text-white"
                        >
                            <Button
                                color="primary"
                                className="d-flex align-items-center"
                                type="button"
                            >
                                Edit trip
                            </Button>
                        </Link>
                    </CardBody>
                </Card>
                <Card>
                    <CardHeader>
                        <CardTitle>Loads</CardTitle>
                    </CardHeader>
                    <CardBody>
                        {this.state.data.loadDtoList &&
                            this.state.data.loadDtoList.map((item) => {
                                return (
                                    <>
                                        &nbsp;
                                        <Link to={`/loads/edit/${item.id}`}>
                                            {item.customLoadNumber}
                                        </Link>
                                        &nbsp;
                                    </>
                                );
                            })}
                    </CardBody>
                </Card>
                <div className="d-flex flex-wrap" style={{ gap: 15 }}>
                    {this.state.data.chronologicalSequence &&
                        this.state.data.chronologicalSequence.map((item) => {
                            if (item.pickupDateFormatted) {
                                return (
                                    <Card style={{ width: "calc(25% - 12px)" }}>
                                        <CardHeader>
                                            <CardTitle>Pickup</CardTitle>
                                        </CardHeader>
                                        <CardBody>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    {
                                                        item.consigneeNameAndLocation
                                                    }
                                                </h6>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">Date:</h6>
                                                <p>
                                                    {item.pickupDateFormatted}
                                                </p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Driver Instructions:
                                                </h6>
                                                <p>{item.driverInstructions}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">BOL:</h6>
                                                <p>{item.bol}</p>
                                            </div>
                                            <div className="mt-1">
                                                {item.bolId && (
                                                    <>
                                                        <h6 className="mb-0">
                                                            BOL file:
                                                        </h6>
                                                        <Button.Ripple
                                                            style={{
                                                                width: 225,
                                                            }}
                                                            className="mt-1"
                                                            type="button"
                                                            href={`${window.location.origin}/file/${item.bolId}`}
                                                            onclick={() =>
                                                                window.open(
                                                                    `${window.location.origin}/file/${item.bolId}`,
                                                                    "_blank"
                                                                )
                                                            }
                                                        >
                                                            Download BOL
                                                        </Button.Ripple>
                                                    </>
                                                )}
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Customer Required Info:
                                                </h6>
                                                <p>{item.customRequiredInfo}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Weight:
                                                </h6>
                                                <p>{item.weight}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Quantity:
                                                </h6>
                                                <p>{item.quantity}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">Notes:</h6>
                                                <p>{item.notes}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Commodity:
                                                </h6>
                                                <p>{item.commodity}</p>
                                            </div>
                                        </CardBody>
                                    </Card>
                                );
                            } else {
                                return (
                                    <Card style={{ width: "calc(25% - 12px)" }}>
                                        <CardHeader>
                                            <CardTitle>Delivery</CardTitle>
                                        </CardHeader>
                                        <CardBody>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    {
                                                        item.consigneeNameAndLocation
                                                    }
                                                </h6>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">Date:</h6>
                                                <p>
                                                    {item.deliveryDateFormatted}
                                                </p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Driver Instructions:
                                                </h6>
                                                <p>{item.driverInstructions}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Customer Required Info:
                                                </h6>
                                                <p>{item.customRequiredInfo}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Weight:
                                                </h6>
                                                <p>{item.weight}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Quantity:
                                                </h6>
                                                <p>{item.quantity}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">Notes:</h6>
                                                <p>{item.notes}</p>
                                            </div>
                                            <div className="mt-1">
                                                <h6 className="mb-0">
                                                    Commodity:
                                                </h6>
                                                <p>{item.commodity}</p>
                                            </div>
                                        </CardBody>
                                    </Card>
                                );
                            }
                        })}
                </div>
            </>
        );
    }
}
export default View;
