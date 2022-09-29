import React from "react";
import {
    Button,
    Modal,
    ModalHeader,
    ModalBody,
    ModalFooter,
    Form,
    FormGroup,
    Input,
    CustomInput,
    Col,
} from "reactstrap";
import * as Icon from "react-feather";
import Flatpickr from "react-flatpickr";
import AsyncSelect from "react-select/async";
import { toast, Flip } from "react-toastify";
import moment from "moment";

class PickupModal extends React.Component {
    state = {
        shipper: null,
        shipperName: "",
        shipperSelected: null,
        bolId: null,
        pickupDate_: null,
        pickupDateWithOffset: null,
        prevBolId: null,
        companies: [],
        searchVal: null,
    };
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
                    if (val === this.state.searchVal) {
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

    componentDidUpdate(prevProps) {
        if (
            this.props.modal !== prevProps.modal &&
            this.props.modal &&
            this.props.editingChildId
        ) {
            fetch(
                process.env.REACT_APP_BASE_URL +
                    `/pickup/${this.props.editingChildId}`,
                {
                    headers: {
                        Authorization: this.props.token,
                    },
                }
            )
                .then((res) => res.json())
                .then((data) => {
                    let selectedValue = {
                        value: data.shipperCompanyId,
                        label: data.shipperCompany,
                    };
                    // let d = new Date();
                    // let utc = data.pickupDate_ + d.getTimezoneOffset() * 60000;
                    // let nd = utc + 3600000 * 5;
                    console.log(data.pickupDate_);
                    this.setState({
                        shipperSelected: selectedValue,
                        pickupDate_: data.pickupDate_,
                        pickupDateWithOffset: data.pickupDate_,
                    });
                    this.shipperChange(selectedValue);
                    document.querySelector("#driverInstructions").value =
                        data.driverInstructions;
                    document.querySelector("#bol").value = data.bol;
                    document.querySelector("#customerRequiredInfo").value =
                        data.customRequiredInfo;
                    document.querySelector("#weight").value = data.weight;
                    document.querySelector("#quantity").value = data.quantity;
                    document.querySelector("#notes").value = data.notes;
                    document.querySelector("#commodity").value = data.commodity;

                    if (data.bolId) {
                        this.setState({ prevBolId: data.bolId });
                    } else {
                        this.setState({ prevBolId: null });
                    }
                });
        } else if (
            this.props.modal !== prevProps.modal &&
            this.props.modal &&
            !this.props.editingChildId
        ) {
            this.setState({
                shipper: null,
                shipperName: "",
                shipperSelected: null,
                bolId: null,
                pickupDate_: null,
                pickupDateWithOffset: null,
            });
            setTimeout(() => {
                document.querySelector("#pickupDate").value = "";
                document.querySelector("#driverInstructions").value = "";
                document.querySelector("#bol").value = "";
                document.querySelector("#customerRequiredInfo").value = "";
                document.querySelector("#weight").value = "";
                document.querySelector("#quantity").value = "";
                document.querySelector("#notes").value = "";
                document.querySelector("#commodity").value = "";
            }, 300);
        }
    }
    deletePrevBol = () => {
        this.setState({ prevBolId: null, bolId: null });
    };
    newPickup = () => {
        let sendingData = {
            shipperCompanyId: this.state.shipper,
            pickupDate_: this.state.pickupDateWithOffset,
            driverInstructions: document.querySelector("#driverInstructions")
                .value,
            bol: document.querySelector("#bol").value,
            customRequiredInfo: document.querySelector("#customerRequiredInfo")
                .value,
            weight: parseInt(document.querySelector("#weight").value),
            quantity: parseInt(document.querySelector("#quantity").value),
            notes: document.querySelector("#notes").value,
            commodity: document.querySelector("#commodity").value,
            bolId: this.state.prevBolId
                ? this.state.prevBolId
                : this.state.bolId,
        };

        if (
            this.props.editingChildId !== null &&
            this.props.editingChildId !== undefined
        ) {
            sendingData.id = this.props.editingChildId;
            fetch(process.env.REACT_APP_BASE_URL + "/pickup/edit", {
                headers: {
                    Authorization: this.props.token,
                    "Content-Type": "application/json",
                },
                method: "PUT",
                body: JSON.stringify(sendingData),
            })
                .then((res) => {
                    if (!res.ok) {
                        throw new Error(res);
                    }
                    return res.text();
                })
                .then((data) => {
                    fetch(
                        process.env.REACT_APP_BASE_URL +
                            `/pickup/resolved_date/${sendingData.pickupDate_}`
                    )
                        .then((res) => res.text())
                        .then((time) => {
                            toast.success("Pickup successfuly edited", {
                                transition: Flip,
                            });
                            this.props.addPickup(
                                this.state.shipperName,
                                time,
                                this.props.editingChildId
                            );
                        });
                })
                .catch((error) => {
                    toast.error("Something went wrong", { transition: Flip });
                    return Promise.reject();
                });
        } else {
            fetch(process.env.REACT_APP_BASE_URL + "/pickup/new", {
                headers: {
                    Authorization: this.props.token,
                    "Content-Type": "application/json",
                },
                method: "POST",
                body: JSON.stringify(sendingData),
            })
                .then((res) => {
                    if (!res.ok) {
                        throw new Error(res);
                    }
                    return res.json();
                })
                .then((data) => {
                    fetch(
                        process.env.REACT_APP_BASE_URL +
                            `/pickup/resolved_date/${sendingData.pickupDate_}`
                    )
                        .then((res) => res.text())
                        .then((time) => {
                            toast.success("Pickup successfuly added", {
                                transition: Flip,
                            });
                            this.props.addPickup(
                                this.state.shipperName,
                                time,
                                data.id,
                                data.has_attachment
                            );
                        });
                })
                .catch((error) => {
                    toast.error("Something went wrong", { transition: Flip });
                    return Promise.reject();
                });
        }
    };

    uploadBol = (file, item) => {
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
                this.setState({ bolId: data });
            });
    };
    shipperChange = (value) => {
        if (value == null) {
            this.setState({
                shipper: null,
                shipperName: "",
                shipperSelected: null,
            });
        } else {
            this.setState({
                shipper: value.value,
                shipperName: value.label,
                shipperSelected: value,
            });
        }
    };

    parseDate(dateString, format) {
        let timeZone = "America/Los_Angeles";
        let timezonedDate = timeZone
            ? new moment.tz(dateString, format, timeZone)
            : new moment(dateString, format);
        return timezonedDate.toDate();
    }

    render() {
        return (
            <Modal
                isOpen={this.props.modal}
                toggle={this.props.togglePickupModal}
                className={this.props.className}
                backdrop="static"
            >
                <ModalHeader toggle={() => this.props.togglePickupModal(null)}>
                    Pickup modal
                </ModalHeader>
                <ModalBody>
                    <Form>
                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>Shipper*</span>
                            </Col>
                            <Col md="6" style={{ zIndex: 10000 }}>
                                <AsyncSelect
                                    noOptionsMessage={(value) =>
                                        !value.inputValue
                                            ? "type something to search"
                                            : "nothing to show"
                                    }
                                    placeholder="Search"
                                    isClearable={true}
                                    defaultValue={null}
                                    loadOptions={this.props.loadOptions}
                                    value={this.state.shipperSelected}
                                    onChange={this.shipperChange}
                                    theme={(theme) => ({
                                        ...theme,
                                        colors: {
                                            ...theme.colors,
                                            primary50: "#fe810b",
                                            primary25: "rgb(253, 179, 46)",
                                            primary: "rgb(253, 179, 46)",
                                        },
                                    })}
                                />
                            </Col>
                            <Col md="2 pl-0">
                                <Button
                                    color="success"
                                    type="button"
                                    onClick={() =>
                                        this.props.toggleNewCompany()
                                    }
                                >
                                    <Icon.Plus size={22} />
                                </Button>
                            </Col>
                        </FormGroup>
                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>Pickup Date</span>
                            </Col>
                            <Col md="8">
                                <Flatpickr
                                    value={this.state.pickupDate_}
                                    id="pickupDate"
                                    className="form-control"
                                    data-enable-time
                                    options={{
                                        dateFormat: "Z",
                                        altInput: true,
                                        altFormat: "m-d-Y H:i",
                                        clickOpens: this.props.canBeChanged,
                                        parseDate: this.parseDate,
                                    }}
                                    onChange={(e) => {
                                        let utc =
                                            e[0].getTime() -
                                            e[0].getTimezoneOffset() * 60000;
                                        let nd = utc - 3600000 * 5;
                                        console.log(e[0]);
                                        console.log(e[0].getTime());
                                        console.log(Date.parse(e[0]));
                                        console.log(nd);
                                        this.setState({
                                            pickupDate_: Date.parse(e[0]),
                                            pickupDateWithOffset:
                                                e[0].getTime(),
                                        });
                                    }}
                                />
                            </Col>
                        </FormGroup>

                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>Driver Instructions</span>
                            </Col>
                            <Col md="8">
                                <Input type="text" id="driverInstructions" />
                            </Col>
                        </FormGroup>

                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>BOL</span>
                            </Col>
                            <Col md="8">
                                <Input type="text" id="bol" />
                            </Col>
                        </FormGroup>
                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>BOL File</span>
                            </Col>

                            {this.state.prevBolId ? (
                                <>
                                    <Col md="8">
                                        <div className="d-flex align-items-center justify-content-end">
                                            <Button.Ripple
                                                style={{ width: 225 }}
                                                className="mt-1"
                                                type="button"
                                                href={`${window.location.origin}/file/${this.state.prevBolId}`}
                                                onclick={() =>
                                                    window.open(
                                                        `${window.location.origin}/file/${this.state.prevBolId}`,
                                                        "_blank"
                                                    )
                                                }
                                            >
                                                Download BOL file
                                            </Button.Ripple>
                                            <Button.Ripple
                                                className="btn-icon mt-1"
                                                color="red"
                                                type="button"
                                                onClick={() =>
                                                    this.deletePrevBol()
                                                }
                                            >
                                                <Icon.Trash2 />
                                            </Button.Ripple>
                                        </div>
                                    </Col>
                                </>
                            ) : (
                                <Col md="8">
                                    <CustomInput
                                        type="file"
                                        onInput={(e) =>
                                            this.uploadBol(e.target.files[0])
                                        }
                                    />
                                </Col>
                            )}
                        </FormGroup>

                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>Customer Required Info</span>
                            </Col>
                            <Col md="8">
                                <Input type="text" id="customerRequiredInfo" />
                            </Col>
                        </FormGroup>
                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>Weight</span>
                            </Col>
                            <Col md="8">
                                <Input type="text" id="weight" />
                            </Col>
                        </FormGroup>
                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>Quantity</span>
                            </Col>
                            <Col md="8">
                                <Input type="text" id="quantity" />
                            </Col>
                        </FormGroup>
                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>Notes</span>
                            </Col>
                            <Col md="8">
                                <Input type="text" id="notes" />
                            </Col>
                        </FormGroup>
                        <FormGroup className="align-items-center" row>
                            <Col md="4">
                                <span>Commodity</span>
                            </Col>
                            <Col md="8">
                                <Input type="text" id="commodity" />
                            </Col>
                        </FormGroup>
                    </Form>
                </ModalBody>
                <ModalFooter>
                    <Button color="primary" onClick={() => this.newPickup()}>
                        Accept
                    </Button>{" "}
                </ModalFooter>
            </Modal>
        );
    }
}
export default PickupModal;
