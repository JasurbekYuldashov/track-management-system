import React from "react";
import {
    Form,
    Button,
    FormGroup,
    Input,
    Label,
    CustomInput,
    InputGroup,
    InputGroupAddon,
    InputGroupText,
    Row,
    Col,
    Card,
    CardBody,
    CardTitle,
    CardHeader,
} from "reactstrap";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import PickupModal from "./Modals/Pickup";
import ActivePickup from "./Components/ActivePickup.js";
import ActiveDelivery from "./Components/ActiveDelivery.js";
import DeliveryModal from "./Modals/Delivery.js";
import NewCompany from "./Modals/NewCompany.js";
import AsyncSelect from "react-select/async";
import { toast, Flip } from "react-toastify";
import { Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
import { AiOutlineUnlock, AiOutlineLock } from "react-icons/ai";

class EditLoads extends React.Component {
    state = {
        states: [],
        companies: [],
        customer: null,
        showPickupModal: false,
        showNewCompanyModal: false,
        showDeliveryModal: false,
        editingChildId: null,
        activePickups: [],
        activeDeliveries: [],
        canBeChanged: false,
        owned_companies: [],
        owned_company: null,
        rateConfirmationId: null,
        prevRateConfirmationId: null,
        RevisedRateConfirmationId: null,
        prevRevisedRateConfirmationId: null,
        searchVal: null,
        loading: true,
    };

    addPickup = (shipper, date, id, has_attachment) => {
        let array = [...this.state.activePickups];
        let existing = array.find((el) => el.id === id);
        let index = array.indexOf(existing);
        if (index > -1) {
            for (let i = array.length - 1; i >= 0; --i) {
                if (array[i].id === id) {
                    array.splice(i, 1);
                }
            }
        }
        let obj = {
            shipper,
            date,
            id,
            has_attachment,
        };
        array.push(obj);
        this.setState({
            activePickups: array,
        });
        this.togglePickupModal();
        this.newLoadOnCloseModal();
    };
    addDelivery = (consignee, date, id) => {
        let array = [...this.state.activeDeliveries];
        let existing = array.find((el) => el.id === id);
        let index = array.indexOf(existing);
        if (index > -1) {
            for (let i = array.length - 1; i >= 0; --i) {
                if (array[i].id === id) {
                    array.splice(i, 1);
                }
            }
        }
        let obj = {
            consignee,
            date,
            id,
        };
        array.push(obj);
        this.setState({
            activeDeliveries: array,
        });
        this.toggleDeliveryModal(null);
        this.newLoadOnCloseModal();
    };

    uploadFile = (file, item) => {
        let formData = new FormData();
        formData.append("file", file);
        if (file === undefined) {
            return;
        }
        fetch(process.env.REACT_APP_BASE_URL + "/file/upload", {
            headers: {
                Authorization: this.props.token,
            },
            method: "POST",
            body: formData,
        })
            .then((res) => res.json())
            .then((data) => {
                if (item === "rc") this.setState({ rateConfirmationId: data });
                if (item === "rrc")
                    this.setState({ revisedRateConfirmationId: data });
            });
    };

    deletePickup = (id) => {
        let array = [...this.state.activePickups];
        for (let i = array.length - 1; i >= 0; --i) {
            if (array[i].id == id) {
                array.splice(i, 1);
            }
        }
        this.setState({
            activePickups: array,
        });
    };
    deleteDelivery = (id) => {
        let array = [...this.state.activeDeliveries];
        for (let i = array.length - 1; i >= 0; --i) {
            if (array[i].id == id) {
                array.splice(i, 1);
            }
        }
        this.setState({
            activeDeliveries: array,
        });
    };
    togglePickupModal = (editingChildId) => {
        this.setState((prevState) => ({
            showPickupModal: !prevState.showPickupModal,
            editingChildId,
        }));
    };
    toggleNewCompany = () => {
        this.setState((prevState) => ({
            showNewCompanyModal: !prevState.showNewCompanyModal,
        }));
    };
    toggleDeliveryModal = (editingChildId) => {
        this.setState((prevState) => ({
            showDeliveryModal: !prevState.showDeliveryModal,
            editingChildId,
        }));
    };
    newLoad = () => {
        let deliveries = this.state.activeDeliveries.map((el) => el.id);
        let pickups = this.state.activePickups.map((el) => el.id);
        let data = {
            id: parseInt(this.props.match.params.id),
            customLoadNumber:
                document.querySelector("#customId").value +
                "-" +
                this.state.abbreviation,
            customerId: this.state.customerId,
            deliveries,
            pickups,
            ownedCompanyId: parseInt(
                document.querySelector("#owned_company").value
            ),
            rateConfirmationId: this.state.rateConfirmationId
                ? this.state.rateConfirmationId
                : this.state.prevRateConfirmationId,
            revisedRateConfirmationId: this.state.revisedRateConfirmationId
                ? this.state.revisedRateConfirmationId
                : this.state.prevRevisedRateConfirmationId,
            rcPrice: document.querySelector("#rc_price").value,
            revisedRcPrice: document.querySelector("#rrc_price").value,
        };
        fetch(process.env.REACT_APP_BASE_URL + "/load/edit", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "PUT",
            body: JSON.stringify(data),
        }).then((res) => {
            {
                if (res.ok) {
                    toast.success("Load successfuly edited", {
                        transition: Flip,
                    });
                    window.history.back();
                } else {
                    let result = res.json();
                    try {
                        result.then((data) => {
                            toast.error(
                                data.error_message
                                    ? data.error_message
                                    : "Something went wrong",
                                {
                                    transition: Flip,
                                }
                            );
                        });
                    } catch (err) {
                        console.log(err);
                    }
                }
            }
        });
    };

    newLoadOnCloseModal = () => {
        let deliveries = this.state.activeDeliveries.map((el) => el.id);
        let pickups = this.state.activePickups.map((el) => el.id);
        let data = {
            id: parseInt(this.props.match.params.id),
            customLoadNumber:
                document.querySelector("#customId").value +
                "-" +
                this.state.abbreviation,
            customerId: this.state.customerId,
            deliveries,
            pickups,
            ownedCompanyId: parseInt(
                document.querySelector("#owned_company").value
            ),
            rateConfirmationId: this.state.rateConfirmationId
                ? this.state.rateConfirmationId
                : this.state.prevRateConfirmationId,
            revisedRateConfirmationId: this.state.revisedRateConfirmationId
                ? this.state.revisedRateConfirmationId
                : this.state.prevRevisedRateConfirmationId,
            rcPrice: document.querySelector("#rc_price").value,
            revisedRcPrice: document.querySelector("#rrc_price").value,
        };
        fetch(process.env.REACT_APP_BASE_URL + "/load/edit", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "PUT",
            body: JSON.stringify(data),
        }).then((res) => {
            {
                if (res.ok) {
                    toast.success("Load successfuly edited", {
                        transition: Flip,
                    });
                } else {
                    let result = res.json();
                    try {
                        result.then((data) => {
                            toast.error(
                                data.error_message
                                    ? data.error_message
                                    : "Something went wrong",
                                {
                                    transition: Flip,
                                }
                            );
                        });
                    } catch (err) {
                        console.log(err);
                    }
                }
            }
        });
    };

    calculate = (type) => {
        let additional = document.querySelector("#additional").value
            ? parseFloat(document.querySelector("#additional").value)
            : 0;
        let booked = document.querySelector("#booked").value
            ? parseFloat(document.querySelector("#booked").value)
            : 0;
        let detention = document.querySelector("#detention").value
            ? parseFloat(document.querySelector("#detention").value)
            : 0;
        let dispute = document.querySelector("#dispute").value
            ? parseFloat(document.querySelector("#dispute").value)
            : 0;
        let factoring = document.querySelector("#factoring").value
            ? parseFloat(document.querySelector("#factoring").value)
            : 0;
        let fine = document.querySelector("#fine").value
            ? parseFloat(document.querySelector("#fine").value)
            : 0;
        let tafs = document.querySelector("#tafs").value
            ? parseFloat(document.querySelector("#tafs").value)
            : 0;

        if (type == 0) {
            let result = booked + dispute + detention + additional - fine;
            document.querySelector("#revisedInvoice").value = result;
            return;
        }
        let result = (factoring - tafs).toFixed(3);
        document.querySelector("#netPaid").value = parseFloat(result);
    };

    postPayments = () => {
        let data = {
            loadId: this.props.match.params.id,
            additional: document.querySelector("#additional").value
                ? document.querySelector("#additional").value
                : 0,
            booked: document.querySelector("#booked").value
                ? document.querySelector("#booked").value
                : 0,
            detention: document.querySelector("#detention").value
                ? document.querySelector("#detention").value
                : 0,
            dispute: document.querySelector("#dispute").value
                ? document.querySelector("#dispute").value
                : 0,
            factoring: document.querySelector("#factoring").value
                ? document.querySelector("#factoring").value
                : 0,
            fine: document.querySelector("#fine").value
                ? document.querySelector("#fine").value
                : 0,
            netPaid: document.querySelector("#netPaid").value
                ? document.querySelector("#netPaid").value
                : 0,
            revisedInvoice: document.querySelector("#revisedInvoice").value
                ? document.querySelector("#revisedInvoice").value
                : 0,
            tafs: document.querySelector("#tafs").value
                ? document.querySelector("#tafs").value
                : 0,
        };
        fetch(process.env.REACT_APP_BASE_URL + `/counting/on_load`, {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("Payments successfuly edited", {
                    transition: Flip,
                });
            } else {
                let result = res.json();
                try {
                    result.then((data) => {
                        toast.error(
                            data.error_message
                                ? data.error_message
                                : "Something went wrong",
                            {
                                transition: Flip,
                            }
                        );
                    });
                } catch (err) {
                    toast.error(
                        data.error_message
                            ? data.error_message
                            : "Something went wrong",
                        {
                            transition: Flip,
                        }
                    );
                }
            }
        });
    };

    componentDidMount() {
        fetch(process.env.REACT_APP_BASE_URL + "/state_province/all", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    states: data,
                });
                fetch(process.env.REACT_APP_BASE_URL + "/load/context", {
                    headers: {
                        Authorization: this.props.token,
                    },
                })
                    .then((res) => res.json())
                    .then((data) => {
                        this.setState({
                            owned_companies: data.owned_companies,
                        });
                        this.setState({
                            abbreviation: data.owned_companies[0].abbreviation,
                        });
                        fetch(
                            process.env.REACT_APP_BASE_URL +
                                `/load/${this.props.match.params.id}`,
                            {
                                headers: {
                                    Authorization: this.props.token,
                                },
                            }
                        )
                            .then((res) => res.json())
                            .then((data) => {
                                let pickups = [];
                                let deliveries = [];
                                data.pickupsInitialized.forEach((item) => {
                                    let obj = {
                                        shipper: item.consigneeNameAndLocation,
                                        date: item.pickupDateFormatted,
                                        id: item.id,
                                        has_attachment: item.bolId,
                                    };
                                    pickups.push(obj);
                                });

                                data.deliveriesInitialized.forEach((item) => {
                                    let obj = {
                                        consignee:
                                            item.consigneeNameAndLocation,
                                        date: item.deliveryDateFormatted,
                                        id: item.id,
                                    };
                                    deliveries.push(obj);
                                });
                                let selectedValue = this.state.companies.find(
                                    (item) => item.value == data.customerId
                                );
                                this.setState({
                                    activePickups: pickups,
                                    activeDeliveries: deliveries,
                                    customerId: data.customerId,
                                    customerSelected: selectedValue,
                                    canBeChanged:
                                        data.canBeChanged ||
                                        this.props.userRole === "admin",
                                    canBeChangedInitial: data.canBeChanged,
                                    prevRateConfirmationId:
                                        data.rateConfirmationId,
                                    prevRevisedRateConfirmationId:
                                        data.revisedRateConfirmationId,
                                    loading: false,
                                });

                                this.setAbbreviation(data.ownedCompanyId);
                                document.querySelector("#customId").value =
                                    data.customLoadNumber_;
                                document.querySelector("#owned_company").value =
                                    data.ownedCompanyId;
                                document.querySelector("#rc_price").value =
                                    data.rcPrice;
                                document.querySelector("#rrc_price").value =
                                    data.revisedRcPrice;
                                let company = {
                                    value: data.customerId,
                                    label: data.customer,
                                };
                                this.setState({
                                    customerId: company.value,
                                    customerSelected: company,
                                });
                                fetch(
                                    process.env.REACT_APP_BASE_URL +
                                        `/counting/on_load/${this.props.match.params.id}`,
                                    {
                                        headers: {
                                            Authorization: this.props.token,
                                        },
                                    }
                                )
                                    .then((res) => res.json())
                                    .then((data) => {
                                        document.querySelector(
                                            "#additional"
                                        ).value = data.additional;
                                        document.querySelector(
                                            "#booked"
                                        ).value = data.booked;
                                        document.querySelector(
                                            "#detention"
                                        ).value = data.detention;
                                        document.querySelector(
                                            "#dispute"
                                        ).value = data.dispute;
                                        document.querySelector(
                                            "#factoring"
                                        ).value = data.factoring;
                                        document.querySelector("#fine").value =
                                            data.fine;
                                        document.querySelector(
                                            "#netPaid"
                                        ).value = data.netPaid;
                                        document.querySelector(
                                            "#revisedInvoice"
                                        ).value = data.revisedInvoice;
                                        document.querySelector("#tafs").value =
                                            data.tafs;
                                    });
                            });
                    });
            });
    }
    companySearch = (val) => {
        if (val) {
            this.setState({
                searchVal: val,
            });
            fetch(process.env.REACT_APP_BASE_URL + `/company/search?q=${val}`, {
                headers: {
                    Authorization: this.props.token,
                },
            })
                .then((res) => res.json())
                .then((data) => {
                    if (val == this.state.searchVal) {
                        let dataToShow = [];
                        data.forEach((el) => {
                            let elToShow = {
                                value: el.id,
                                label:
                                    el.companyName +
                                    ", " +
                                    (el.cityDto &&
                                        el.cityDto.nameWithParentAnsi),
                            };
                            dataToShow.push(elToShow);
                        });
                        this.setState({
                            companies: dataToShow,
                        });
                    }
                });
        } else {
            this.setState({
                companies: [],
            });
        }
    };
    loadOptions = (inputValue, callback) => {
        fetch(
            process.env.REACT_APP_BASE_URL + `/company/search?q=${inputValue}`
        )
            .then((res) => res.json())
            .then((data) =>
                callback(
                    data.map((el) => {
                        return {
                            ...el,
                            value: el.id,
                            label:
                                el.companyName +
                                ", " +
                                (el.cityDto && el.cityDto.nameWithParentAnsi),
                        };
                    })
                )
            );
    };
    setAbbreviation = (e) => {
        let abbreviation = this.state.owned_companies.find(
            (item) => item.id == e
        );
        this.setState({
            abbreviation: abbreviation && abbreviation.abbreviation,
        });
    };
    render() {
        return (
            <>
                <Card>
                    <CardHeader>
                        <h3 className="mb-0">
                            Editing Load{" "}
                            {this.state.canBeChangedInitial ? (
                                <AiOutlineUnlock />
                            ) : (
                                <AiOutlineLock />
                            )}
                        </h3>
                    </CardHeader>
                    <CardBody>
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
                            <Form>
                                <h4 className="mb-0">Basic Details</h4>
                                <FormGroup
                                    className="align-items-center mt-2"
                                    row
                                >
                                    <Col md="4">
                                        <span>Company*</span>
                                    </Col>
                                    <Col md="8">
                                        <CustomInput
                                            type="select"
                                            name="select"
                                            id="owned_company"
                                            onChange={(e) =>
                                                this.setAbbreviation(
                                                    e.target.value
                                                )
                                            }
                                        >
                                            {this.state.owned_companies.map(
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
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Custom Load Number*</span>
                                    </Col>
                                    <Col md="8">
                                        <InputGroup>
                                            <Input
                                                placeholder=""
                                                id="customId"
                                            />
                                            <InputGroupAddon addonType="append">
                                                <InputGroupText>
                                                    {this.state.abbreviation}
                                                </InputGroupText>
                                            </InputGroupAddon>
                                        </InputGroup>
                                    </Col>
                                </FormGroup>

                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Customer*</span>
                                    </Col>
                                    <Col md="6" style={{ zIndex: 100 }}>
                                        <AsyncSelect
                                            noOptionsMessage={(value) =>
                                                !value.inputValue
                                                    ? "type something to search"
                                                    : "nothing to show"
                                            }
                                            placeholder="Search"
                                            isClearable={true}
                                            defaultValue={null}
                                            loadOptions={this.loadOptions}
                                            value={this.state.customerSelected}
                                            onChange={(value) => {
                                                if (value !== null) {
                                                    this.setState({
                                                        customerId: value.value,
                                                        customerSelected: value,
                                                    });
                                                } else {
                                                    this.setState({
                                                        customerId: null,
                                                        customerSelected: null,
                                                    });
                                                }
                                            }}
                                            theme={(theme) => ({
                                                ...theme,
                                                colors: {
                                                    ...theme.colors,
                                                    primary50: "#fe810b",
                                                    primary25:
                                                        "rgb(253, 179, 46)",
                                                    primary:
                                                        "rgb(253, 179, 46)",
                                                },
                                            })}
                                        />
                                    </Col>
                                    <Col md="2 pl-0">
                                        <Button.Ripple
                                            color="success"
                                            type="button"
                                            onClick={() =>
                                                this.toggleNewCompany()
                                            }
                                        >
                                            <Icon.Plus size={22} />
                                        </Button.Ripple>
                                    </Col>
                                </FormGroup>

                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>Rate Confirmation</span>
                                    </Col>
                                    {this.state.prevRateConfirmationId ? (
                                        <>
                                            <Col md="2">
                                                <div className="d-flex align-items-center justify-content-start">
                                                    <Button.Ripple
                                                        style={{ width: 250 }}
                                                        className="mt-1"
                                                        type="button"
                                                        href={`${window.location.origin}/file/${this.state.prevRateConfirmationId}`}
                                                        onclick={() =>
                                                            window.open(
                                                                `${window.location.origin}/file/${this.state.prevRateConfirmationId}`,
                                                                "_blank"
                                                            )
                                                        }
                                                    >
                                                        Download Rate
                                                        Confirmation
                                                    </Button.Ripple>
                                                    <Button.Ripple
                                                        className="btn-icon mt-1"
                                                        color="red"
                                                        type="button"
                                                        onClick={() =>
                                                            this.setState({
                                                                prevRateConfirmationId:
                                                                    null,
                                                            })
                                                        }
                                                    >
                                                        <Icon.Trash2 />
                                                    </Button.Ripple>
                                                </div>
                                            </Col>
                                        </>
                                    ) : (
                                        <Col md="2">
                                            <CustomInput
                                                id="file1"
                                                type="file"
                                                onInput={(e) =>
                                                    this.uploadFile(
                                                        e.target.files[0],
                                                        "rc"
                                                    )
                                                }
                                            />
                                        </Col>
                                    )}
                                    <Col md="2" className="text-right">
                                        <span>Revised RC</span>
                                    </Col>
                                    {this.state
                                        .prevRevisedRateConfirmationId ? (
                                        <>
                                            <Col md="2">
                                                <div className="d-flex align-items-center justify-content-start">
                                                    <Button.Ripple
                                                        style={{ width: 250 }}
                                                        className="mt-1"
                                                        type="button"
                                                        href={`${window.location.origin}/file/${this.state.prevRevisedRateConfirmationId}`}
                                                        onclick={() =>
                                                            window.open(
                                                                `${window.location.origin}/file/${this.state.prevRevisedRateConfirmationId}`,
                                                                "_blank"
                                                            )
                                                        }
                                                    >
                                                        Download Revised Rate
                                                        Confirmation
                                                    </Button.Ripple>
                                                    <Button.Ripple
                                                        className="btn-icon mt-1"
                                                        color="red"
                                                        type="button"
                                                        onClick={() =>
                                                            this.setState({
                                                                prevRevisedRateConfirmationId:
                                                                    null,
                                                            })
                                                        }
                                                    >
                                                        <Icon.Trash2 />
                                                    </Button.Ripple>
                                                </div>
                                            </Col>
                                        </>
                                    ) : (
                                        <Col md="2">
                                            <CustomInput
                                                id="file2"
                                                type="file"
                                                onInput={(e) =>
                                                    this.uploadFile(
                                                        e.target.files[0],
                                                        "rrc"
                                                    )
                                                }
                                            />
                                        </Col>
                                    )}
                                </FormGroup>
                                <FormGroup className="align-items-center" row>
                                    <Col md="4">
                                        <span>RC Price</span>
                                    </Col>
                                    <Col md="2">
                                        <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                            <Input
                                                type="number"
                                                placeholder="0.00"
                                                id="rc_price"
                                            />
                                            <div className="form-control-position">
                                                <Icon.DollarSign />
                                            </div>
                                        </FormGroup>
                                    </Col>
                                    <Col md="2" className="text-right">
                                        <span>Revised RC Price</span>
                                    </Col>
                                    <Col md="2">
                                        <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                            <Input
                                                type="number"
                                                placeholder="0.00"
                                                id="rrc_price"
                                            />
                                            <div className="form-control-position">
                                                <Icon.DollarSign />
                                            </div>
                                        </FormGroup>
                                    </Col>
                                </FormGroup>
                            </Form>
                        )}
                    </CardBody>
                </Card>
                <Row>
                    <Col sm="12" md="6">
                        <Card>
                            <CardHeader>
                                <CardTitle>Pickup</CardTitle>
                            </CardHeader>
                            <CardBody>
                                <div className="flex-column">
                                    {this.state.activePickups.map((item) => (
                                        <ActivePickup
                                            key={item.id}
                                            data={item}
                                            deletePickup={(id) =>
                                                this.deletePickup(id)
                                            }
                                            editPickup={this.togglePickupModal}
                                            canBeChanged={
                                                this.state.canBeChanged
                                            }
                                            has_attachment={item.has_attachment}
                                        />
                                    ))}
                                </div>
                                <Button
                                    color="success"
                                    className="d-flex align-items-center"
                                    type="button"
                                    onClick={() => this.togglePickupModal(null)}
                                >
                                    <Icon.Plus size={22} />
                                    Add new pickup
                                </Button>

                                <PickupModal
                                    modal={this.state.showPickupModal}
                                    togglePickupModal={this.togglePickupModal}
                                    toggleNewCompany={this.toggleNewCompany}
                                    companies={this.state.companies}
                                    token={this.props.token}
                                    canBeChanged={this.state.canBeChanged}
                                    addPickup={this.addPickup}
                                    editingChildId={this.state.editingChildId}
                                    loadOptions={this.loadOptions}
                                />
                                <NewCompany
                                    modal={this.state.showNewCompanyModal}
                                    toggleNewCompany={this.toggleNewCompany}
                                    token={this.props.token}
                                />
                            </CardBody>
                        </Card>
                    </Col>
                    <Col sm="12" md="6">
                        <Card>
                            <CardHeader>
                                <CardTitle>Delivery</CardTitle>
                            </CardHeader>
                            <CardBody>
                                {this.state.activeDeliveries.map((item) => (
                                    <ActiveDelivery
                                        key={item.id}
                                        data={item}
                                        canBeChanged={this.state.canBeChanged}
                                        deleteDelivery={(id) =>
                                            this.deleteDelivery(id)
                                        }
                                        editDelivery={this.toggleDeliveryModal}
                                    />
                                ))}
                                <Button
                                    color="success"
                                    className="d-flex align-items-center"
                                    type="button"
                                    onClick={() =>
                                        this.toggleDeliveryModal(null)
                                    }
                                >
                                    <Icon.Plus size={22} />
                                    Add new delivery
                                </Button>

                                <DeliveryModal
                                    loadOptions={this.loadOptions}
                                    modal={this.state.showDeliveryModal}
                                    toggleDeliveryModal={
                                        this.toggleDeliveryModal
                                    }
                                    companies={this.state.companies}
                                    token={this.props.token}
                                    addDelivery={this.addDelivery}
                                    canBeChanged={this.state.canBeChanged}
                                    toggleNewCompany={this.toggleNewCompany}
                                    editingChildId={this.state.editingChildId}
                                />
                            </CardBody>
                        </Card>
                    </Col>
                </Row>
                <div className="d-flex justify-content-center">
                    <Button
                        color="success"
                        className="d-flex align-items-center"
                        type="button"
                        onClick={() => this.newLoad()}
                    >
                        <Icon.Check size={22} />
                        Save
                    </Button>
                </div>
                {this.props.userRole === "admin" && (
                    <Row
                        className="justify-content-center"
                        style={{ marginTop: "2.2rem" }}
                    >
                        <Col md="6" sm="12">
                            <Card>
                                <CardHeader>
                                    <CardTitle tag="h4">Payments</CardTitle>
                                </CardHeader>
                                <CardBody>
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
                                        <Form>
                                            <Row>
                                                <Col sm="6">
                                                    <Label for="nameVertical">
                                                        Booked
                                                    </Label>
                                                    <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                                        <Input
                                                            type="number"
                                                            placeholder="0.00"
                                                            id="booked"
                                                            onChange={() =>
                                                                this.calculate(
                                                                    0
                                                                )
                                                            }
                                                        />
                                                        <div className="form-control-position">
                                                            <Icon.DollarSign />
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                                <Col sm="6">
                                                    <Label for="nameVertical">
                                                        Dispute
                                                    </Label>
                                                    <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                                        <Input
                                                            type="number"
                                                            placeholder="0.00"
                                                            id="dispute"
                                                            onChange={() =>
                                                                this.calculate(
                                                                    0
                                                                )
                                                            }
                                                        />
                                                        <div className="form-control-position">
                                                            <Icon.DollarSign />
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                                <Col sm="6">
                                                    <Label for="nameVertical">
                                                        Detention
                                                    </Label>
                                                    <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                                        <Input
                                                            type="number"
                                                            placeholder="0.00"
                                                            id="detention"
                                                            onChange={() =>
                                                                this.calculate(
                                                                    0
                                                                )
                                                            }
                                                        />
                                                        <div className="form-control-position">
                                                            <Icon.DollarSign />
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                                <Col sm="6">
                                                    <Label for="nameVertical">
                                                        Additional
                                                    </Label>
                                                    <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                                        <Input
                                                            type="number"
                                                            placeholder="0.00"
                                                            id="additional"
                                                            onChange={() =>
                                                                this.calculate(
                                                                    0
                                                                )
                                                            }
                                                        />
                                                        <div className="form-control-position">
                                                            <Icon.DollarSign />
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                                <Col sm="6">
                                                    <Label for="nameVertical">
                                                        Fine
                                                    </Label>
                                                    <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                                        <Input
                                                            type="number"
                                                            placeholder="0.00"
                                                            id="fine"
                                                            onChange={() =>
                                                                this.calculate(
                                                                    0
                                                                )
                                                            }
                                                        />
                                                        <div className="form-control-position">
                                                            <Icon.DollarSign />
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                                <Col sm="6">
                                                    <Label for="nameVertical">
                                                        Revised / Invoice
                                                    </Label>
                                                    <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                                        <Input
                                                            disabled
                                                            type="number"
                                                            placeholder="0.00"
                                                            id="revisedInvoice"
                                                        />
                                                        <div className="form-control-position">
                                                            <Icon.DollarSign />
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                                <hr className="w-100 mt-2" />
                                                <Col sm="6">
                                                    <Label for="nameVertical">
                                                        Factoring
                                                    </Label>
                                                    <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                                        <Input
                                                            type="number"
                                                            placeholder="0.00"
                                                            id="factoring"
                                                            onChange={() =>
                                                                this.calculate(
                                                                    1
                                                                )
                                                            }
                                                        />
                                                        <div className="form-control-position">
                                                            <Icon.DollarSign />
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                                <Col sm="6">
                                                    <Label for="nameVertical">
                                                        Tafs
                                                    </Label>
                                                    <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                                        <Input
                                                            type="number"
                                                            placeholder="0"
                                                            id="tafs"
                                                            onChange={() =>
                                                                this.calculate(
                                                                    1
                                                                )
                                                            }
                                                        />
                                                        <div className="form-control-position">
                                                            <Icon.DollarSign />
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                                <Col sm="6">
                                                    <Label
                                                        for="nameVertical"
                                                        className="bold"
                                                    >
                                                        Net Paid
                                                    </Label>
                                                    <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                                                        <Input
                                                            disabled
                                                            type="number"
                                                            placeholder="0.00"
                                                            id="netPaid"
                                                        />
                                                        <div className="form-control-position">
                                                            <Icon.DollarSign />
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                                <Col sm="12">
                                                    <FormGroup className="d-flex mb-0">
                                                        <div className="mt-2">
                                                            <Button.Ripple
                                                                color="primary"
                                                                type="button"
                                                                onClick={() =>
                                                                    this.postPayments()
                                                                }
                                                            >
                                                                Submit
                                                            </Button.Ripple>
                                                        </div>
                                                    </FormGroup>
                                                </Col>
                                            </Row>
                                        </Form>
                                    )}
                                </CardBody>
                            </Card>
                        </Col>
                    </Row>
                )}
            </>
        );
    }
}
const mapStateToProps = (state) => {
    return {
        token: state.auth.login.token,
        userRole: state.auth.login.userRole,
    };
};
export default connect(mapStateToProps)(EditLoads);
