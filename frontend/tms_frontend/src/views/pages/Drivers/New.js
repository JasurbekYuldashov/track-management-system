import React from "react";
import {
    Form,
    Button,
    FormGroup,
    Input,
    Label,
    CustomInput,
    Col,
    Card,
    CardBody,
    CardHeader,
} from "reactstrap";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { toast, Flip } from "react-toastify";
import Flatpickr from "react-flatpickr";
class NewDriver extends React.Component {
    state = {
        states: [],
        paymentTypes: [],
        licenseFileId: null,
        medicalCardFileId: null,
        trucks: [],
        driverStatuses: [],
        socialSecurityFileId: null,
    };
    newDriver = () => {
        let data = {
            socialSecurityFileId: this.state.socialSecurityFileId,
            alternatePhone: document.querySelector("#alternatePhone").value,
            city: document.querySelector("#city").value,
            defaultPaymentTypeId: parseInt(
                document.querySelector("#defaultPaymentType").value
            ),
            email: document.querySelector("#email").value,
            firstName: document.querySelector("#firstName").value,
            hireDate: document.querySelector("#hireDate").value,
            lastName: document.querySelector("#lastName").value,
            licenseExpiration:
                document.querySelector("#licenseExpiration").value,
            licenseIssuedJurisdictionId: document.querySelector(
                "#licenseIssuingState"
            ).value,
            licenseNumber: document.querySelector("#licenseNumber").value,
            medicalCardRenewal: document.querySelector("#medicalCardRenewal")
                .value,
            phone: document.querySelector("#phone").value,
            stateProvinceId: parseInt(document.querySelector("#state").value),
            street: document.querySelector("#street").value,
            terminationDate: document.querySelector("#terminationDate").value,
            zipCode: document.querySelector("#zipCode").value,
            licenseFileId: this.state.licenseFileId,
            medicalCardFileId: this.state.medicalCardFileId,
            note: document.querySelector("#note").value,
            truckId: parseInt(document.querySelector("#truck").value),
            driverStatusId: parseInt(document.querySelector("#status").value),
        };
        fetch(process.env.REACT_APP_BASE_URL + "/driver/new", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("Driver successfuly added", { transition: Flip });
                window.history.back();
            } else {
                toast.error("Something went wrong", { transition: Flip });
                res.text();
            }
        });
    };
    uploadLicense = (file) => {
        let formData = new FormData();
        formData.append("file", file, "license");

        fetch(process.env.REACT_APP_BASE_URL + "/file/upload", {
            headers: {
                Authorization: this.props.token,
            },
            method: "POST",
            body: formData,
        })
            .then((res) => res.json())
            .then((data) =>
                this.setState({
                    licenseFileId: data,
                })
            );
    };

    uploadSSN = (file) => {
        let formData = new FormData();
        formData.append("file", file, file.name);

        fetch(process.env.REACT_APP_BASE_URL + "/file/upload", {
            headers: {
                Authorization: this.props.token,
            },
            method: "POST",
            body: formData,
        })
            .then((res) => res.json())
            .then((data) =>
                this.setState({
                    socialSecurityFileId: data,
                })
            );
    };

    uploadMedicalCart = (file) => {
        let formData = new FormData();
        formData.append("file", file, "cart");

        fetch(process.env.REACT_APP_BASE_URL + "/file/upload", {
            headers: {
                Authorization: this.props.token,
            },
            method: "POST",
            body: formData,
        })
            .then((res) => res.json())
            .then((data) =>
                this.setState({
                    medicalCardFileId: data,
                })
            );
    };
    componentDidMount() {
        fetch(process.env.REACT_APP_BASE_URL + "/state_province/all", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) =>
                this.setState({
                    states: data,
                })
            );
        fetch(process.env.REACT_APP_BASE_URL + "/driver/context", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                let statuses = data.driver_statuses;
                let removeValFromIndex = [0, 1, 3];
                for (let i = removeValFromIndex.length - 1; i >= 0; i--)
                    statuses.splice(removeValFromIndex[i], 1);

                this.setState({
                    paymentTypes: data.payment_types,
                    driverStatuses: statuses,
                });
            });
        fetch(process.env.REACT_APP_BASE_URL + "/unit/list?size=10000", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({ trucks: data.content });
            });
    }
    render() {
        return (
            <Card>
                <CardHeader>
                    <h3 className="mb-0">Adding a new Driver</h3>
                </CardHeader>
                <CardBody>
                    <Form className="d-flex">
                        <div style={{ width: "50%", marginRight: 20 }}>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>First Name</span>
                                </Col>
                                <Col md="8">
                                    <Input type="text" id="firstName" />
                                </Col>
                            </FormGroup>

                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Last Name*</span>
                                </Col>
                                <Col md="8">
                                    <Input type="text" id="lastName" />
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Status</span>
                                </Col>
                                <Col md="8">
                                    <CustomInput
                                        type="select"
                                        name="select"
                                        id="status"
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
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center mt-3" row>
                                <Col md="4">
                                    <span>Truck</span>
                                </Col>
                                <Col md="8">
                                    <CustomInput
                                        type="select"
                                        name="select"
                                        id="truck"
                                    >
                                        {this.state.trucks.map((item) => (
                                            <option
                                                key={item.id}
                                                value={item.id}
                                            >
                                                {item.number}
                                            </option>
                                        ))}
                                    </CustomInput>
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Street</span>
                                </Col>
                                <Col md="8">
                                    <Input type="text" id="street" />
                                </Col>
                            </FormGroup>

                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>City*</span>
                                </Col>
                                <Col md="8">
                                    <Input type="text" id="city" />
                                </Col>
                            </FormGroup>

                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>State/Province*</span>
                                </Col>
                                <Col md="8">
                                    <CustomInput
                                        type="select"
                                        name="select"
                                        id="state"
                                    >
                                        {this.state.states.map((item) => (
                                            <option
                                                key={item.id}
                                                value={item.id}
                                            >
                                                {item.name}
                                            </option>
                                        ))}
                                    </CustomInput>
                                </Col>
                            </FormGroup>

                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Email</span>
                                </Col>
                                <Col md="8">
                                    <Input type="text" id="email" />
                                </Col>
                            </FormGroup>

                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Default Payment Type*</span>
                                </Col>
                                <Col md="8">
                                    <CustomInput
                                        type="select"
                                        name="select"
                                        id="defaultPaymentType"
                                    >
                                        {this.state.paymentTypes.map((item) => (
                                            <option
                                                key={item.id}
                                                value={item.id}
                                            >
                                                {item.name}
                                            </option>
                                        ))}
                                    </CustomInput>
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>License number</span>
                                </Col>
                                <Col md="8">
                                    <Input type="text" id="licenseNumber" />
                                </Col>
                            </FormGroup>

                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>License Expiration</span>
                                </Col>
                                <Col md="8">
                                    <Flatpickr
                                        id="licenseExpiration"
                                        className="form-control"
                                    />
                                </Col>
                            </FormGroup>
                            <FormGroup>
                                <Label for="customFile">Note</Label>
                                <Input
                                    type="textarea"
                                    id="note"
                                    placeholder=""
                                    maxLength="1000"
                                />
                            </FormGroup>
                        </div>
                        <div style={{ width: "50%" }}>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Zip Code</span>
                                </Col>
                                <Col md="8">
                                    <Input type="text" id="zipCode" />
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Phone</span>
                                </Col>
                                <Col md="8">
                                    <Input
                                        type="text"
                                        id="phone"
                                        placeholder=""
                                    />
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Alternate Phone</span>
                                </Col>
                                <Col md="8">
                                    <Input
                                        type="text"
                                        id="alternatePhone"
                                        placeholder=""
                                    />
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>
                                        License issuing state/jurisdiction
                                    </span>
                                </Col>
                                <Col md="8">
                                    <CustomInput
                                        type="select"
                                        name="select"
                                        id="licenseIssuingState"
                                    >
                                        {this.state.states.map((item) => (
                                            <option
                                                key={item.id}
                                                value={item.id}
                                            >
                                                {item.name}
                                            </option>
                                        ))}
                                    </CustomInput>
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Medical Card Renewal</span>
                                </Col>
                                <Col md="8">
                                    <Flatpickr
                                        id="medicalCardRenewal"
                                        className="form-control"
                                    />
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Hire Date</span>
                                </Col>
                                <Col md="8">
                                    <Flatpickr
                                        id="hireDate"
                                        className="form-control"
                                    />
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Termination Date</span>
                                </Col>
                                <Col md="8">
                                    <Flatpickr
                                        id="terminationDate"
                                        className="form-control"
                                    />
                                </Col>
                            </FormGroup>
                            {/* <FormGroup className="align-items-center" row>
                <Col md="4">
                  <span>Social security number</span>
                </Col>
                <Col md="8">
                  <Input type="text" id="ssn" placeholder="" />
                </Col>
              </FormGroup> */}
                            <FormGroup>
                                <Label for="customFile">License</Label>
                                <CustomInput
                                    type="file"
                                    id="licenseFile"
                                    onInput={(e) =>
                                        this.uploadLicense(e.target.files[0])
                                    }
                                />
                            </FormGroup>
                            <FormGroup>
                                <Label for="customFile">Medical Cart</Label>
                                <CustomInput
                                    type="file"
                                    id="medicalCart"
                                    onInput={(e) =>
                                        this.uploadMedicalCart(
                                            e.target.files[0]
                                        )
                                    }
                                />
                            </FormGroup>
                            <FormGroup>
                                <Label for="customFile">SSN</Label>
                                <CustomInput
                                    type="file"
                                    id="ssn"
                                    onInput={(e) =>
                                        this.uploadSSN(e.target.files[0])
                                    }
                                />
                            </FormGroup>
                        </div>
                    </Form>
                    <Button
                        color="success"
                        className="d-flex align-items-center"
                        type="button"
                        onClick={() => this.newDriver()}
                    >
                        <Icon.Check size={22} /> Save driver
                    </Button>
                </CardBody>
            </Card>
        );
    }
}
const mapStateToProps = (state) => {
    return {
        token: state.auth.login.token,
    };
};
export default connect(mapStateToProps)(NewDriver);
