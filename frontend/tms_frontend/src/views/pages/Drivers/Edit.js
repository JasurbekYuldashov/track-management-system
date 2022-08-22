import React from "react";
import {
    // Form,
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
    // Button
} from "reactstrap";
import * as Icon from "react-feather";
import {connect} from "react-redux";
import {toast, Flip} from "react-toastify";
import Flatpickr from "react-flatpickr";
import {Spin} from "antd";
import {LoadingOutlined} from "@ant-design/icons";

class EditDriver extends React.Component {
    state = {
        status: null,
        states: [],
        paymentTypes: [],
        licenseFileId: null,
        medicalCardFileId: null,
        socialSecurityFileId: null,
        data: [],
        trucks: [],
        driverStatuses: [],
        loading: true,
    };
    setStatus = (id) => {
        fetch(`/driver/edit-status/${this.props.match.params.id}/${id}`, {
            headers: {
                Authorization: this.props.token,
            },
            method: "PUT",
        }).then((res) => {
            if (res.ok) {
                toast.success("Status successfuly changed", {transition: Flip});
                this.setState({
                    status: id,
                });
            } else {
                toast.error("Something went wrong", {transition: Flip});
                res.text();
            }
        });
    };
    uploadSSN = (file) => {
        let formData = new FormData();
        formData.append("file", file, file.name);

        fetch("/file/upload", {
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
    editDriver = () => {
        let data = {
            id: this.props.match.params.id,
            alternatePhone: document.querySelector("#alternatePhone").value,
            city: document.querySelector("#city").value,
            defaultPaymentTypeId: parseInt(
                document.querySelector("#defaultPaymentType").value
            ),
            email: document.querySelector("#email").value,
            firstName: document.querySelector("#firstName").value,
            hireDate: document.querySelector("#hireDate").value,
            lastName: document.querySelector("#lastName").value,
            licenseExpiration: document.querySelector("#licenseExpiration").value,
            licenseIssuedJurisdictionId: document.querySelector(
                "#licenseIssuingState"
            ).value,
            licenseNumber: document.querySelector("#licenseNumber").value,
            medicalCardRenewal: document.querySelector("#medicalCardRenewal").value,
            phone: document.querySelector("#phone").value,
            stateProvinceId: parseInt(document.querySelector("#state").value),
            street: document.querySelector("#street").value,
            terminationDate: document.querySelector("#terminationDate").value,
            zipCode: document.querySelector("#zipCode").value,
            licenseFileId: this.state.licenseFileId,
            medicalCardFileId: this.state.medicalCardFileId,
            socialSecurityFileId: this.state.socialSecurityFileId,
            note: document.querySelector("#note").value,
            truckId: parseInt(document.querySelector("#truck").value),
            driverStatusId: parseInt(document.querySelector("#status").value),
        };
        fetch("/driver/edit", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "PUT",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("Driver successfuly edited", {transition: Flip});
                window.history.back();
            } else {
                toast.error("Something went wrong", {transition: Flip});
                res.text();
            }
        });
    };
    uploadLicense = (file) => {
        let formData = new FormData();
        formData.append("file", file, file.name);

        fetch("/file/upload", {
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
    uploadMedicalCart = (file) => {
        let formData = new FormData();
        formData.append("file", file, file.name);

        fetch("/file/upload", {
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
        fetch("/state_province/all", {
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
        fetch("/driver/context", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    paymentTypes: data.payment_types,
                    driverStatuses: data.driver_statuses,
                });
                fetch("/unit/list?size=10000", {
                    headers: {
                        Authorization: this.props.token,
                    },
                })
                    .then((res) => res.json())
                    .then((data) => {
                        this.setState({trucks: data.content});
                        fetch(`/driver/${this.props.match.params.id}`, {
                            headers: {
                                Authorization: this.props.token,
                            },
                        })
                            .then((res) => res.json())
                            .then((data) => {
                                this.setState({
                                    licenseFileId: data.licenseFileId,
                                    medicalCardFileId: data.medicalCardFileId,
                                    status: data.driverStatusId,
                                    socialSecurityFileId: data.socialSecurityFileId,
                                    loading: false,
                                });
                                document.querySelector("#alternatePhone").value =
                                    data.alternatePhone;
                                document.querySelector("#city").value = data.city;
                                document.querySelector("#defaultPaymentType").value =
                                    data.defaultPaymentTypeId;
                                document.querySelector("#email").value = data.email;
                                document.querySelector("#firstName").value = data.firstName;
                                document.querySelector("#hireDate").value = data.hireDate;
                                document.querySelector("#lastName").value = data.lastName;
                                document.querySelector("#licenseExpiration").value =
                                    data.licenseExpirationFormatted;
                                document.querySelector("#licenseIssuingState").value =
                                    data.licenseIssuedJurisdictionId;
                                document.querySelector("#licenseNumber").value =
                                    data.licenseNumber;
                                document.querySelector("#medicalCardRenewal").value =
                                    data.medicalCardRenewalFormatted;
                                document.querySelector("#phone").value = data.phone;
                                document.querySelector("#state").value = data.stateProvinceId;
                                document.querySelector("#street").value = data.street;
                                document.querySelector("#terminationDate").value =
                                    data.terminationDateFormatted;
                                document.querySelector("#zipCode").value = data.zipCode;
                                this.setState({licenseFileId: data.licenseFileId});
                                this.setState({medicalCardFileId: data.medicalCardFileId});
                                document.querySelector("#note").value = data.note;
                                document.querySelector("#status").value = data.driverStatusId;
                                document.querySelector("#truck").value = data.truckId;
                            });
                    });
            });
    }

    deleteLicense = () => {
        this.setState({licenseFileId: null});
    };

    deleteSSN = () => {
        this.setState({socialSecurityFileId: null});
    };

    deleteMedicalCard = () => {
        this.setState({medicalCardFileId: null});
    };

    render() {
        return (
            <>
                <Card>
                    <CardHeader>
                        <h3 className="mb-0">Editing Driver</h3>
                    </CardHeader>
                    <CardBody>
                        {this.state.loading ? (
                            <Spin
                                indicator={<LoadingOutlined style={{fontSize: 44}} spin/>}
                                style={{
                                    height: "calc(100vh - 20rem)",
                                    width: "100%",
                                    display: "flex",
                                    justifyContent: "center",
                                    alignItems: "center",
                                }}
                            />
                        ) : (
                            <>
                                {" "}
                                <Form className="d-flex">
                                    <div style={{width: "50%", marginRight: 20}}>
                                        <FormGroup className="align-items-center" row>
                                            <Col md="4">
                                                <span>First Name</span>
                                            </Col>
                                            <Col md="8">
                                                <Input type="text" id="firstName"/>
                                            </Col>
                                        </FormGroup>

                                        <FormGroup className="align-items-center" row>
                                            <Col md="4">
                                                <span>Last Name*</span>
                                            </Col>
                                            <Col md="8">
                                                <Input type="text" id="lastName"/>
                                            </Col>
                                        </FormGroup>
                                        <FormGroup className="align-items-center" row>
                                            <Col md="4">
                                                <span>Status</span>
                                            </Col>
                                            <Col md="8">
                                                <CustomInput type="select" name="select" id="status">
                                                    {this.state.driverStatuses.map((item) => (
                                                        <option key={item.id} value={item.id}>
                                                            {item.name}
                                                        </option>
                                                    ))}
                                                </CustomInput>
                                            </Col>
                                        </FormGroup>

                                        <FormGroup className="align-items-center mt-3" row>
                                            <Col md="4">
                                                <span>Truck</span>
                                            </Col>
                                            <Col md="8">
                                                <CustomInput type="select" name="select" id="truck">
                                                    {this.state.trucks.map((item) => (
                                                        <option key={item.id} value={item.id}>
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
                                                <Input type="text" id="street"/>
                                            </Col>
                                        </FormGroup>

                                        <FormGroup className="align-items-center" row>
                                            <Col md="4">
                                                <span>City*</span>
                                            </Col>
                                            <Col md="8">
                                                <Input type="text" id="city"/>
                                            </Col>
                                        </FormGroup>

                                        <FormGroup className="align-items-center" row>
                                            <Col md="4">
                                                <span>State/Province*</span>
                                            </Col>
                                            <Col md="8">
                                                <CustomInput type="select" name="select" id="state">
                                                    {this.state.states.map((item) => (
                                                        <option key={item.id} value={item.id}>
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
                                                <Input type="text" id="email"/>
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
                                                        <option key={item.id} value={item.id}>
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
                                                <Input type="text" id="licenseNumber"/>
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
                                    <div style={{width: "50%"}}>
                                        <FormGroup className="align-items-center" row>
                                            <Col md="4">
                                                <span>Zip Code</span>
                                            </Col>
                                            <Col md="8">
                                                <Input type="text" id="zipCode"/>
                                            </Col>
                                        </FormGroup>
                                        <FormGroup className="align-items-center" row>
                                            <Col md="4">
                                                <span>Phone</span>
                                            </Col>
                                            <Col md="8">
                                                <Input type="text" id="phone" placeholder=""/>
                                            </Col>
                                        </FormGroup>
                                        <FormGroup className="align-items-center" row>
                                            <Col md="4">
                                                <span>Alternate Phone</span>
                                            </Col>
                                            <Col md="8">
                                                <Input type="text" id="alternatePhone" placeholder=""/>
                                            </Col>
                                        </FormGroup>
                                        <FormGroup className="align-items-center" row>
                                            <Col md="4">
                                                <span>License issuing state/jurisdiction</span>
                                            </Col>
                                            <Col md="8">
                                                <CustomInput
                                                    type="select"
                                                    name="select"
                                                    id="licenseIssuingState"
                                                >
                                                    {this.state.states.map((item) => (
                                                        <option key={item.id} value={item.id}>
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
                                                <Flatpickr id="hireDate" className="form-control"/>
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
                                        {this.state.licenseFileId ? (
                                            <>
                                                <div className="d-flex align-items-center justify-content-end">
                                                    <Button.Ripple
                                                        style={{width: 225}}
                                                        className="mt-1"
                                                        type="button"
                                                        href={`${window.location.origin}/file/${this.state.licenseFileId}`}
                                                        onclick={() =>
                                                            window.open(
                                                                `${window.location.origin}/file/${this.state.licenseFileId}`,
                                                                "_blank"
                                                            )
                                                        }
                                                    >
                                                        Download License
                                                    </Button.Ripple>
                                                    <Button.Ripple
                                                        className="btn-icon mt-1"
                                                        color="red"
                                                        type="button"
                                                        onClick={() => this.deleteLicense()}
                                                    >
                                                        <Icon.Trash2/>
                                                    </Button.Ripple>
                                                </div>
                                            </>
                                        ) : (
                                            <FormGroup>
                                                <Label for="customFile">Upload License</Label>
                                                <CustomInput
                                                    type="file"
                                                    id="licenseFile"
                                                    onInput={(e) => this.uploadLicense(e.target.files[0])}
                                                />
                                            </FormGroup>
                                        )}
                                        {this.state.medicalCardFileId ? (
                                            <>
                                                <div className="d-flex align-items-center justify-content-end">
                                                    <Button.Ripple
                                                        style={{width: 225}}
                                                        className="mt-1"
                                                        type="button"
                                                        href={`${window.location.origin}/file/${this.state.medicalCardFileId}`}
                                                        onclick={() =>
                                                            window.open(
                                                                `${window.location.origin}/file/${this.state.medicalCardFileId}`,
                                                                "_blank"
                                                            )
                                                        }
                                                    >
                                                        Download Medical Cart
                                                    </Button.Ripple>
                                                    <Button.Ripple
                                                        className="btn-icon mt-1"
                                                        color="red"
                                                        type="button"
                                                        onClick={() => this.deleteMedicalCard()}
                                                    >
                                                        <Icon.Trash2/>
                                                    </Button.Ripple>
                                                </div>
                                            </>
                                        ) : (
                                            <FormGroup>
                                                <Label for="customFile">Medical Cart</Label>
                                                <CustomInput
                                                    type="file"
                                                    id="medicalCart"
                                                    onInput={(e) =>
                                                        this.uploadMedicalCart(e.target.files[0])
                                                    }
                                                />
                                            </FormGroup>
                                        )}
                                        {this.state.socialSecurityFileId ? (
                                            <>
                                                <div className="d-flex align-items-center justify-content-end">
                                                    <Button.Ripple
                                                        style={{width: 225}}
                                                        className="mt-1"
                                                        type="button"
                                                        href={`${window.location.origin}/file/${this.state.socialSecurityFileId}`}
                                                        onclick={() =>
                                                            window.open(
                                                                `${window.location.origin}/file/${this.state.socialSecurityFileId}`,
                                                                "_blank"
                                                            )
                                                        }
                                                    >
                                                        Download SSN
                                                    </Button.Ripple>
                                                    <Button.Ripple
                                                        className="btn-icon mt-1"
                                                        color="red"
                                                        type="button"
                                                        onClick={() => this.deleteSSN()}
                                                    >
                                                        <Icon.Trash2/>
                                                    </Button.Ripple>
                                                </div>
                                            </>
                                        ) : (
                                            <FormGroup>
                                                <Label for="customFile">SSN</Label>
                                                <CustomInput
                                                    type="file"
                                                    id="ssn"
                                                    onInput={(e) => this.uploadSSN(e.target.files[0])}
                                                />
                                            </FormGroup>
                                        )}
                                    </div>
                                </Form>
                                <Button
                                    color="success"
                                    className="d-flex align-items-center"
                                    type="button"
                                    onClick={() => this.editDriver()}
                                >
                                    <Icon.Check size={22}/> Save
                                </Button>
                            </>
                        )}
                    </CardBody>
                </Card>
                <Card style={{width: "30%", minWidth: 400}}>
                    <CardBody>
                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>Status</span>
                            </Col>
                            <Col md="8">
                                <CustomInput
                                    type="select"
                                    name="select"
                                    onChange={(e) => this.setStatus(e.target.value)}
                                    value={this.state.status}
                                >
                                    {this.state.driverStatuses.map((item) => (
                                        <option key={item.id} value={item.id}>
                                            {item.name}
                                        </option>
                                    ))}
                                </CustomInput>
                            </Col>
                        </FormGroup>
                    </CardBody>
                </Card>
            </>
        );
    }
}

const mapStateToProps = (state) => {
    return {
        token: state.auth.login.token,
    };
};
export default connect(mapStateToProps)(EditDriver);
