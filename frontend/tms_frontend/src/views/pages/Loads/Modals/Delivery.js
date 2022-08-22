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
  Col,
} from "reactstrap";
import * as Icon from "react-feather";
import Flatpickr from "react-flatpickr";
import AsyncSelect from "react-select/async";
import { toast, Flip } from "react-toastify";
class PDeliveryModal extends React.Component {
  state = {
    consignee: null,
    consigneeName: "",
    consigneeSelected: null,
    deliveryDate_: null,
    deliveryDateWithOffset: null,
    companies: [],
    searchVal: null,
  };
  componentDidUpdate(prevProps) {
    if (
      this.props.modal !== prevProps.modal &&
      this.props.modal &&
      this.props.editingChildId
    ) {
      fetch(`/delivery/${this.props.editingChildId}`, {
        headers: {
          Authorization: this.props.token,
        },
      })
        .then((res) => res.json())
        .then((data) => {
          let selectedValue = {
            value: data.consigneeCompanyId,
            label: data.consigneeCompany,
          };

          let d = new Date();
          let utc = data.deliveryDate_ + d.getTimezoneOffset() * 60000;
          let nd = utc + 3600000 * 5;
          this.setState({
            consigneeSelected: selectedValue,
            deliveryDate_: nd,
            deliveryDateWithOffset: data.deliveryDate_,
          });
          this.shipperChange(selectedValue);
          document.querySelector("#driverInstructions").value =
            data.driverInstructions;
          document.querySelector("#customerRequiredInfo").value =
            data.customRequiredInfo;
          document.querySelector("#weight").value = data.weight;
          document.querySelector("#quantity").value = data.quantity;
          document.querySelector("#notes").value = data.notes;
          document.querySelector("#commodity").value = data.commodity;
        });
    } else if (
      this.props.modal !== prevProps.modal &&
      this.props.modal &&
      !this.props.editingChildId
    ) {
      this.setState({
        consignee: null,
        consigneeName: "",
        consigneeSelected: null,
        deliveryDate_: null,
        deliveryDateWithOffset: null,
      });
      setTimeout(() => {
        document.querySelector("#deliveryDate").value = "";
        document.querySelector("#driverInstructions").value = "";
        document.querySelector("#customerRequiredInfo").value = "";
        document.querySelector("#weight").value = "";
        document.querySelector("#quantity").value = "";
        document.querySelector("#notes").value = "";
        document.querySelector("#commodity").value = "";
      }, 300);
    }
  }
  companySearch = (val) => {
    if (val) {
      this.setState({
        searchVal: val,
      });
      fetch(`/company/search?q=${val}`, {
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
                  (el.cityDto && el.cityDto.nameWithParentAnsi),
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
  newDelivery = () => {
    let sendingData = {
      consigneeCompanyId: this.state.consignee,
      deliveryDate_: this.state.deliveryDateWithOffset,
      driverInstructions: document.querySelector("#driverInstructions").value,
      customRequiredInfo: document.querySelector("#customerRequiredInfo").value,
      weight: parseInt(document.querySelector("#weight").value),
      quantity: parseInt(document.querySelector("#quantity").value),
      notes: document.querySelector("#notes").value,
      commodity: document.querySelector("#commodity").value,
    };
    if (
      this.props.editingChildId !== null &&
      this.props.editingChildId !== undefined
    ) {
      sendingData.id = this.props.editingChildId;
      fetch("/delivery/edit", {
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
          fetch(`/pickup/resolved_date/${sendingData.deliveryDate_}`)
            .then((res) => res.text())
            .then((time) => {
              toast.success("Delivery successfuly edited", {
                transition: Flip,
              });
              this.props.addDelivery(
                this.state.consigneeName,
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
      fetch("/delivery/new", {
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
          fetch(`/pickup/resolved_date/${sendingData.deliveryDate_}`)
            .then((res) => res.text())
            .then((time) => {
              toast.success("Delivery successfuly added", { transition: Flip });
              this.props.addDelivery(this.state.consigneeName, time, data);
            });
        })
        .catch((error) => {
          toast.error("Something went wrong", { transition: Flip });
          return Promise.reject();
        });
    }
  };

  shipperChange = (value) => {
    if (value == null) {
      this.setState({
        consignee: null,
        consigneeName: "",
        consigneeSelected: null,
      });
    } else {
      this.setState({
        consignee: value.value,
        consigneeName: value.label,
        consigneeSelected: value,
      });
    }
  };

  render() {
    return (
      <Modal
        isOpen={this.props.modal}
        toggle={this.props.toggleDeliveryModal}
        className={this.props.className}
        backdrop="static"
      >
        <ModalHeader toggle={() => this.props.toggleDeliveryModal(null)}>
          New delivery
        </ModalHeader>
        <ModalBody>
          <Form>
            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>Consignee*</span>
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
                  value={this.state.consigneeSelected}
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
                <Button.Ripple
                  color="success"
                  type="button"
                  onClick={() => this.props.toggleNewCompany()}
                >
                  <Icon.Plus size={22} />
                </Button.Ripple>
              </Col>
            </FormGroup>
            <FormGroup className="align-items-center" row aria-readonly={true}>
              <Col md="4">
                <span>Delivery Date</span>
              </Col>
              <Col md="8">
                <Flatpickr
                  id="deliveryDate"
                  className="form-control"
                  data-enable-time
                  options={{
                    dateFormat: "Z",
                    altInput: true,
                    clickOpens: this.props.canBeChanged,
                    altFormat: "m-d-Y H:i",
                  }}
                  value={this.state.deliveryDate_}
                  onChange={(e) => {
                    let utc = e[0].getTime() - e[0].getTimezoneOffset() * 60000;
                    let nd = utc - 3600000 * 5;
                    this.setState({
                      deliveryDate_: Date.parse(e[0]),
                      deliveryDateWithOffset: nd,
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
          <Button color="primary" onClick={() => this.newDelivery()}>
            Accept
          </Button>{" "}
        </ModalFooter>
      </Modal>
    );
  }
}
export default PDeliveryModal;
