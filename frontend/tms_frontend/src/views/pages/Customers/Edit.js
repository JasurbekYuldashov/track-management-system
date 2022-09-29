import React from "react";
import {
    // Form,
    Form,
    Button,
    FormGroup,
    Input,
    CustomInput,
    Col,
    Card,
    CardBody,
    CardHeader,
    // Button
} from "reactstrap";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { toast, Flip } from "react-toastify";
import OfficeModal from "./Components/officeModal";
import AsyncSelect from "react-select/async";
import { Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
class NewCompany extends React.Component {
    state = {
        states: [],
        customer_types: [],
        logoFileId: null,
        showOfficeModal: false,
        offices: [],
        stateProvince: null,
        defaultStateProvince: null,
        loading: true,
    };
    addOffice = (stateProvinceId, city, id) => {
        let array = [...this.state.offices];
        let obj = {
            stateProvinceId,
            city,
            id,
        };
        array.push(obj);
        this.setState({
            offices: array,
        });
        this.toggleOfficeModal();
    };
    newCompany = () => {
        let data = {
            id: this.props.match.params.id,
            alternatePhone: document.querySelector("#alternatePhone").value,
            alternatePhoneExtensionNumber: document.querySelector(
                "#alternatePhoneExtensionNumber"
            ).value,
            aptSuiteOther: document.querySelector("#aptSuiteOther").value,
            companyName: document.querySelector("#companyName").value,
            contact: document.querySelector("#contact").value,
            customerTypeId: parseInt(
                document.querySelector("#customerType").value
            ),
            email: document.querySelector("#email").value,
            fax: document.querySelector("#fax").value,
            motorCarrierNumber: document.querySelector("#motorCarrierNumber")
                .value,
            notes: document.querySelector("#notes").value,
            phoneExtensionNumber: document.querySelector(
                "#phoneExtensionNumber"
            ).value,
            phoneNumber: document.querySelector("#phoneNumber").value,
            locationId: this.state.stateProvince,
            street: document.querySelector("#street").value,
            taxId: document.querySelector("#taxId").value,
            webSite: document.querySelector("#webSite").value,
            zipCode: document.querySelector("#zipCode").value,
        };
        fetch(process.env.REACT_APP_BASE_URL + "/company/edit", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "PUT",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("Company successfuly edited", {
                    transition: Flip,
                });
                window.history.back();
            } else {
                toast.error("Something went wrong", { transition: Flip });
                res.text();
            }
        });
    };
    onDrop = (pictureFiles, pictureDataURLs) => {
        if (pictureFiles.length === 0) {
            return;
        }
        let formData = new FormData();
        formData.append("file", pictureFiles[0], pictureFiles[0].name);

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
                    logoFileId: data,
                })
            );
    };

    updateOffices = () => {
        fetch(
            process.env.REACT_APP_BASE_URL +
                `/company/offices/${this.props.match.params.id}`,
            {
                headers: {
                    Authorization: this.props.token,
                },
            }
        )
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    offices: data.offices,
                });
            });
    };
    componentDidMount() {
        fetch(process.env.REACT_APP_BASE_URL + "/company/context", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    customer_types: data.customer_types,
                    states: data.state_province,
                });
                fetch(
                    process.env.REACT_APP_BASE_URL +
                        `/company/${this.props.match.params.id}`,
                    {
                        headers: {
                            Authorization: this.props.token,
                        },
                    }
                )
                    .then((res) => res.json())
                    .then((data) => {
                        this.setState({
                            loading: false,
                        });
                        document.querySelector("#alternatePhone").value =
                            data.alternatePhone;
                        document.querySelector(
                            "#alternatePhoneExtensionNumber"
                        ).value = data.alternatePhoneExtensionNumber;
                        document.querySelector("#aptSuiteOther").value =
                            data.aptSuiteOther;
                        document.querySelector("#companyName").value =
                            data.companyName;
                        document.querySelector("#contact").value = data.contact;
                        document.querySelector("#customerType").value =
                            data.customerTypeId;
                        document.querySelector("#email").value = data.email;
                        document.querySelector("#fax").value = data.fax;
                        document.querySelector("#motorCarrierNumber").value =
                            data.motorCarrierNumber;
                        document.querySelector("#notes").value = data.notes;
                        document.querySelector("#phoneExtensionNumber").value =
                            data.phoneExtensionNumber;
                        document.querySelector("#phoneNumber").value =
                            data.phoneNumber;
                        document.querySelector("#street").value = data.street;
                        document.querySelector("#taxId").value = data.taxId;
                        document.querySelector("#webSite").value = data.webSite;
                        document.querySelector("#zipCode").value = data.zipCode;
                        this.setState({
                            stateProvince: data.locationId,
                        });
                        if (data.locationId) {
                            fetch(
                                process.env.REACT_APP_BASE_URL +
                                    `/location/by_id/${data.locationId}`
                            )
                                .then((res) => res.json())
                                .then((data) => {
                                    this.setState({
                                        defaultStateProvince: {
                                            value: data.id,
                                            label: data.nameWithParentAnsi,
                                        },
                                    });
                                });
                        }
                    });
            });

        this.updateOffices();
    }
    deleteOffice = (id) => {
        fetch(process.env.REACT_APP_BASE_URL + `/company/${id}`, {
            headers: {
                Authorization: this.props.token,
            },
            method: "DELETE",
        }).then((res) => {
            if (res.ok) {
                this.updateOffices();
            }
        });
    };
    toggleOfficeModal = () => {
        this.setState((prevState) => ({
            showOfficeModal: !prevState.showOfficeModal,
        }));
    };
    loadOptions = (inputValue, callback) => {
        fetch(
            process.env.REACT_APP_BASE_URL + `/location/search?q=${inputValue}`
        )
            .then((res) => res.json())
            .then((data) =>
                callback(
                    data.data.map((el) => {
                        return {
                            value: el.id,
                            label: el.nameWithParentAnsi,
                        };
                    })
                )
            );
    };
    render() {
        return (
            <>
                <Card>
                    <CardHeader>
                        <h3 className="mb-0">Adding a new Customer</h3>
                    </CardHeader>
                    <CardBody className="d-flex">
                        {this.state.loading ? (
                            <Spin
                                indicator={
                                    <LoadingOutlined
                                        style={{ fontSize: 44 }}
                                        spin
                                    />
                                }
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
                                <div style={{ marginRight: 20, flex: 1 }}>
                                    <Form>
                                        <FormGroup
                                            className="align-items-center"
                                            row
                                        >
                                            <Col md="4">
                                                <span>Company Name*</span>
                                            </Col>
                                            <Col md="8">
                                                <Input
                                                    type="text"
                                                    id="companyName"
                                                />
                                            </Col>
                                        </FormGroup>
                                        <FormGroup
                                            className="align-items-center"
                                            row
                                        >
                                            <Col md="4">
                                                <span>Customer Type</span>
                                            </Col>
                                            <Col md="8">
                                                <CustomInput
                                                    type="select"
                                                    name="select"
                                                    id="customerType"
                                                >
                                                    {this.state.customer_types.map(
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
                                        <FormGroup
                                            className="align-items-center"
                                            row
                                        >
                                            <Col md="4">
                                                <span>Street</span>
                                            </Col>
                                            <Col md="8">
                                                <Input
                                                    type="text"
                                                    id="street"
                                                />
                                            </Col>
                                        </FormGroup>
                                        <FormGroup
                                            className="align-items-center"
                                            row
                                        >
                                            <Col md="4">
                                                <span>Apt/Suite/Other</span>
                                            </Col>
                                            <Col md="8">
                                                <Input
                                                    type="text"
                                                    id="aptSuiteOther"
                                                />
                                            </Col>
                                        </FormGroup>
                                        <FormGroup
                                            className="align-items-center"
                                            row
                                        >
                                            <Col md="4">
                                                <span>City*</span>
                                            </Col>
                                            <Col md="8">
                                                <AsyncSelect
                                                    noOptionsMessage={(value) =>
                                                        !value.inputValue
                                                            ? "type something to search"
                                                            : "nothing to show"
                                                    }
                                                    placeholder="Search"
                                                    isClearable={true}
                                                    value={
                                                        this.state
                                                            .defaultStateProvince
                                                    }
                                                    loadOptions={
                                                        this.loadOptions
                                                    }
                                                    onChange={(value) => {
                                                        if (value !== null) {
                                                            this.setState({
                                                                stateProvince:
                                                                    value.value,
                                                                defaultStateProvince:
                                                                    value,
                                                            });
                                                        } else
                                                            this.setState({
                                                                stateProvince:
                                                                    null,
                                                                defaultStateProvince:
                                                                    null,
                                                            });
                                                    }}
                                                    theme={(theme) => ({
                                                        ...theme,
                                                        colors: {
                                                            ...theme.colors,
                                                            primary50:
                                                                "#fe810b",
                                                            primary25:
                                                                "rgb(253, 179, 46)",
                                                            primary:
                                                                "rgb(253, 179, 46)",
                                                        },
                                                    })}
                                                />
                                            </Col>
                                        </FormGroup>
                                        <FormGroup
                                            className="align-items-center"
                                            row
                                        >
                                            <Col md="4">
                                                <span>Zip Code</span>
                                            </Col>
                                            <Col md="8">
                                                <Input
                                                    type="text"
                                                    id="zipCode"
                                                />
                                            </Col>
                                        </FormGroup>
                                        <FormGroup
                                            className="align-items-center"
                                            row
                                        >
                                            <Col md="4">
                                                <span>Phone Number</span>
                                            </Col>
                                            <Col md="8">
                                                <Input
                                                    type="text"
                                                    id="phoneNumber"
                                                />
                                            </Col>
                                        </FormGroup>
                                        <FormGroup
                                            className="align-items-center"
                                            row
                                        >
                                            <Col md="4">
                                                <span>
                                                    Phone Extension number
                                                </span>
                                            </Col>
                                            <Col md="8">
                                                <Input
                                                    type="text"
                                                    id="phoneExtensionNumber"
                                                />
                                            </Col>
                                        </FormGroup>
                                        <FormGroup
                                            className="align-items-center"
                                            row
                                        >
                                            <Col md="4">
                                                <span>Alternate Phone</span>
                                            </Col>
                                            <Col md="8">
                                                <Input
                                                    type="text"
                                                    id="alternatePhone"
                                                />
                                            </Col>
                                        </FormGroup>
                                    </Form>
                                    <Button
                                        color="success"
                                        className="d-flex align-items-center"
                                        type="button"
                                        onClick={() => this.newCompany()}
                                    >
                                        <Icon.Check size={22} /> Save
                                    </Button>
                                </div>
                                <div style={{ width: "50%" }}>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>
                                                Alternate Phone Extension number
                                            </span>
                                        </Col>
                                        <Col md="8">
                                            <Input
                                                type="text"
                                                id="alternatePhoneExtensionNumber"
                                            />
                                        </Col>
                                    </FormGroup>

                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Fax</span>
                                        </Col>
                                        <Col md="8">
                                            <Input type="text" id="fax" />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Email</span>
                                        </Col>
                                        <Col md="8">
                                            <Input type="text" id="email" />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Website</span>
                                        </Col>
                                        <Col md="8">
                                            <Input type="text" id="webSite" />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Contact</span>
                                        </Col>
                                        <Col md="8">
                                            <Input type="text" id="contact" />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Notes</span>
                                        </Col>
                                        <Col md="8">
                                            <Input
                                                type="textarea"
                                                id="notes"
                                                maxLength="1000"
                                            />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Motor Carrier Number</span>
                                        </Col>
                                        <Col md="8">
                                            <Input
                                                type="text"
                                                id="motorCarrierNumber"
                                            />
                                        </Col>
                                    </FormGroup>
                                    <FormGroup
                                        className="align-items-center"
                                        row
                                    >
                                        <Col md="4">
                                            <span>Tax ID (EIN#)</span>
                                        </Col>
                                        <Col md="8">
                                            <Input type="text" id="taxId" />
                                        </Col>
                                    </FormGroup>
                                </div>
                            </>
                        )}
                    </CardBody>
                </Card>
                <Card>
                    <CardBody>
                        <div className="d-flex flex-wrap">
                            {this.state.offices.map((item, i) => {
                                if (i > 0) {
                                    return (
                                        <React.Fragment key={item.id}>
                                            <div
                                                key={item.id}
                                                style={{
                                                    display: "flex",
                                                    alignItems: "center",
                                                    flexDirection: "column",
                                                }}
                                            >
                                                <div className="mt-1">
                                                    {
                                                        this.state.states.find(
                                                            (el) =>
                                                                el.id ===
                                                                item.stateProvinceId
                                                        ).name
                                                    }
                                                </div>
                                                <div>{item.city}</div>
                                            </div>
                                            <Button.Ripple
                                                className="btn-icon mt-1 mr-2"
                                                color="red"
                                                type="button"
                                                onClick={() =>
                                                    this.deleteOffice(item.id)
                                                }
                                            >
                                                <Icon.Trash2 />
                                            </Button.Ripple>
                                        </React.Fragment>
                                    );
                                }
                            })}
                        </div>

                        <div style={{ width: "100%" }}>
                            <Button
                                color="success"
                                className="d-flex align-items-center mt-2"
                                type="button"
                                onClick={() => this.toggleOfficeModal(null)}
                            >
                                New office
                            </Button>
                        </div>
                    </CardBody>
                    <OfficeModal
                        modal={this.state.showOfficeModal}
                        states={this.state.states}
                        toggleOfficeModal={this.toggleOfficeModal}
                        parentId={this.props.match.params.id}
                        addOffice={this.addOffice}
                        token={this.props.token}
                    />
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
export default connect(mapStateToProps)(NewCompany);
