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
import Select from "react-select";
import PickupModal from "./Modals/Pickup";
import ActivePickup from "./Components/ActivePickup.js";
import ActiveDelivery from "./Components/ActiveDelivery.js";
import DeliveryModal from "./Modals/Delivery.js";
import NewCompany from "./Modals/NewCompany.js";
import AsyncSelect from "react-select/async";
import { history } from "../../../history";
import {
  ToastContainer,
  toast,
  Slide,
  Zoom,
  Flip,
  Bounce,
} from "react-toastify";

class NewLoads extends React.Component {
  state = {
    states: [],
    companies: [],
    customer: null,
    showPickupModal: false,
    showNewCompanyModal: false,
    showDeliveryModal: false,
    activePickups: [],
    canBeChanged: true,
    activeDeliveries: [],
    owned_companies: [],
    owned_company: null,
    editingChildId: null,
    rateConfirmationId: null,
    revisedRateConfirmationId: null,
    searchVal: null,
  };
  togglePickupModal = (editingChildId) => {
    this.setState((prevState) => ({
      showPickupModal: !prevState.showPickupModal,
      editingChildId,
    }));
  };
  toggleDeliveryModal = (editingChildId) => {
    this.setState((prevState) => ({
      showDeliveryModal: !prevState.showDeliveryModal,
      editingChildId,
    }));
  };
  addPickup = (shipper, date, id, has_attachment) => {
    let array = [...this.state.activePickups];
    let existing = array.find((el) => el.id == id);
    let index = array.indexOf(existing);
    if (index > -1) {
      for (let i = array.length - 1; i >= 0; --i) {
        if (array[i].id == id) {
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
  };
  addDelivery = (consignee, date, id) => {
    let array = [...this.state.activeDeliveries];
    let existing = array.find((el) => el.id == id);
    let index = array.indexOf(existing);
    if (index > -1) {
      for (let i = array.length - 1; i >= 0; --i) {
        if (array[i].id == id) {
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
  toggleNewCompany = () => {
    this.setState((prevState) => ({
      showNewCompanyModal: !prevState.showNewCompanyModal,
    }));
  };
  newLoad = () => {
    let deliveries = this.state.activeDeliveries.map((el) => el.id);
    let pickups = this.state.activePickups.map((el) => el.id);
    let data = {
      customLoadNumber:
        document.querySelector("#customId").value +
        "-" +
        this.state.abbreviation,
      customerId: this.state.customerId,
      deliveries,
      pickups,
      ownedCompanyId: parseInt(document.querySelector("#owned_company").value),
      rateConfirmationId: this.state.rateConfirmationId,
      revisedRateConfirmationId: this.state.revisedRateConfirmationId,
      rcPrice: document.querySelector("#rc_price").value,
      revisedRcPrice: document.querySelector("#rrc_price").value,
    };
    fetch("/load/new", {
      headers: {
        Authorization: this.props.token,
        "Content-Type": "application/json",
      },
      method: "POST",
      body: JSON.stringify(data),
    }).then((res) => {
      {
        if (res.ok) {
          toast.success("Load successfuly added", { transition: Flip });
          window.history.back();
        } else {
          let result = res.json();
          try {
            result.then((data) => {
              toast.error(data.error_message, { transition: Flip });
            });
          } catch (err) {
            console.log(err);
          }
        }
      }
    });
  };
  componentDidMount() {
    fetch("/state_province/all", {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        this.setState({
          states: data,
        });
      });

    fetch("/load/context", {
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
      });
  }
  loadOptions = (inputValue, callback) => {
    fetch(`/company/search?q=${inputValue}`)
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
  uploadFile = (file, item) => {
    let formData = new FormData();
    formData.append("file", file);
    if (file == undefined) {
      return;
    }
    fetch("/file/upload", {
      headers: {
        Authorization: this.props.token,
      },
      method: "POST",
      body: formData,
    })
      .then((res) => res.json())
      .then((data) => {
        if (item == "rc") this.setState({ rateConfirmationId: data });
        if (item == "rrc") this.setState({ revisedRateConfirmationId: data });
      });
  };
  setAbbreviation = (e) => {
    let abbreviation = this.state.owned_companies.find(
      (item) => item.id == e.target.value
    );
    this.setState({
      abbreviation: abbreviation.abbreviation,
    });
  };
  render() {
    return (
      <>
        <Card>
          <CardHeader>
            <h3 className="mb-0">Adding a new Load</h3>
          </CardHeader>
          <CardBody>
            <Form>
              <h4 className="mb-0">Basic Details</h4>
              <FormGroup className="align-items-center mt-2" row>
                <Col md="4">
                  <span>Company*</span>
                </Col>
                <Col md="8">
                  <CustomInput
                    type="select"
                    name="select"
                    id="owned_company"
                    onChange={(e) => this.setAbbreviation(e)}
                  >
                    {this.state.owned_companies.map((item) => (
                      <option key={item.id} value={item.id}>
                        {item.name}
                      </option>
                    ))}
                  </CustomInput>
                </Col>
              </FormGroup>
              <FormGroup className="align-items-center" row>
                <Col md="4">
                  <span>Custom Load Number*</span>
                </Col>
                <Col md="8">
                  <InputGroup>
                    <Input placeholder="" id="customId" />
                    <InputGroupAddon addonType="append">
                      <InputGroupText>{this.state.abbreviation}</InputGroupText>
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
                    onChange={(value) => {
                      if (value !== null) {
                        this.setState({
                          customerId: value.value,
                        });
                      } else
                        this.setState({
                          customerId: null,
                        });
                    }}
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
                    onClick={() => this.toggleNewCompany()}
                  >
                    <Icon.Plus size={22} />
                  </Button.Ripple>
                </Col>
              </FormGroup>
              <FormGroup className="align-items-center" row>
                <Col md="4">
                  <span>Rate confirmation</span>
                </Col>
                <Col md="2">
                  <CustomInput
                    type="file"
                    onInput={(e) => this.uploadFile(e.target.files[0], "rc")}
                  />
                </Col>
                <Col md="2" className="text-right">
                  <span>Revised RC</span>
                </Col>
                <Col md="2">
                  <CustomInput
                    type="file"
                    onInput={(e) => this.uploadFile(e.target.files[0], "rrc")}
                  />
                </Col>
              </FormGroup>
              <FormGroup className="align-items-center" row>
                <Col md="4">
                  <span>RC Price</span>
                </Col>
                <Col md="2">
                  <FormGroup className="position-relative has-icon-left input-divider-left mb-0">
                    <Input type="number" placeholder="0.00" id="rc_price" />
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
                    <Input type="number" placeholder="0.00" id="rrc_price" />
                    <div className="form-control-position">
                      <Icon.DollarSign />
                    </div>
                  </FormGroup>
                </Col>
              </FormGroup>
            </Form>
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
                      canBeChanged={this.state.canBeChanged}
                      deletePickup={(id) => this.deletePickup(id)}
                      editPickup={this.togglePickupModal}
                      has_attachment={item.has_attachment}
                    />
                  ))}
                </div>
                <Button
                  color="success"
                  className="d-flex align-items-center"
                  type="button"
                  onClick={() => {
                    this.togglePickupModal();
                  }}
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
                    deleteDelivery={(id) => this.deleteDelivery(id)}
                    editDelivery={this.toggleDeliveryModal}
                  />
                ))}
                <Button
                  color="success"
                  className="d-flex align-items-center"
                  type="button"
                  onClick={() => this.toggleDeliveryModal()}
                >
                  <Icon.Plus size={22} />
                  Add new delivery
                </Button>

                <DeliveryModal
                  modal={this.state.showDeliveryModal}
                  toggleDeliveryModal={this.toggleDeliveryModal}
                  companies={this.state.companies}
                  token={this.props.token}
                  canBeChanged={this.state.canBeChanged}
                  addDelivery={this.addDelivery}
                  toggleNewCompany={this.toggleNewCompany}
                  editingChildId={this.state.editingChildId}
                  loadOptions={this.loadOptions}
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
            <Icon.Plus size={22} />
            Add new Load
          </Button>
        </div>
      </>
    );
  }
}
const mapStateToProps = (state) => {
  return {
    token: state.auth.login.token,
  };
};
export default connect(mapStateToProps)(NewLoads);
