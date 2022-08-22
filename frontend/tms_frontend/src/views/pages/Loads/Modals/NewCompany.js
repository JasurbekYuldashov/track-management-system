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
  Col
} from "reactstrap";
import {
  toast,
  Flip
} from "react-toastify";
import AsyncSelect from "react-select/async";
class NewCompany extends React.Component {
  state = {
    shipper: null,
    shipperName: "",
    states: [],
    customer_types: [],
    stateProvince: null,
  };

  componentDidMount() {
    fetch("/company/context", {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) =>
        this.setState({
          customer_types: data.customer_types,
          states: data.state_province,
        })
      );
  }
  newCompany = () => {
    let data = {
      alternatePhone: document.querySelector("#alternatePhone").value,
      alternatePhoneExtensionNumber:
        document.querySelector("#alternatePhone").value,
      aptSuiteOther: document.querySelector("#alternatePhone").value,
      companyName: document.querySelector("#companyName").value,
      contact: document.querySelector("#contact").value,
      customerTypeId: parseInt(document.querySelector("#customerType").value),
      email: document.querySelector("#email").value,
      fax: document.querySelector("#fax").value,
      motorCarrierNumber: document.querySelector("#motorCarrierNumber").value,
      notes: document.querySelector("#notes").value,
      phoneExtensionNumber: document.querySelector("#phoneExtensionNumber")
        .value,
      phoneNumber: document.querySelector("#phoneNumber").value,
      locationId: this.state.stateProvince,
      street: document.querySelector("#street").value,
      taxId: document.querySelector("#taxId").value,
      webSite: document.querySelector("#webSite").value,
      zipCode: document.querySelector("#zipCode").value,
    };
    fetch("/company/new", {
      headers: {
        Authorization: this.props.token,
        "Content-Type": "application/json",
      },
      method: "POST",
      body: JSON.stringify(data),
    }).then((res) => {
      if (res.ok) {
        toast.success("Company successfuly added", { transition: Flip });
      } else {
        toast.error("Something went wrong", { transition: Flip });
        res.text();
      }
      this.props.toggleNewCompany();
    });
  };
  loadOptions = (inputValue, callback) => {
    fetch(`/location/search?q=${inputValue}`)
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
      <Modal
        isOpen={this.props.modal}
        toggle={this.props.toggleNewCompany}
        className="modal-dialog-centered modal-lg"
      >
        <ModalHeader toggle={this.props.toggleNewCompany}>
          New company
        </ModalHeader>
        <ModalBody style={{ display: "flex" }}>
          <div style={{ width: "calc(50%-20px)", marginRight: 20 }}>
            <Form>
              <FormGroup className="align-items-center" row>
                <Col md="4">
                  <span>Company Name*</span>
                </Col>
                <Col md="8">
                  <Input type="text" id="companyName" />
                </Col>
              </FormGroup>
              <FormGroup className="align-items-center" row>
                <Col md="4">
                  <span>Customer Type</span>
                </Col>
                <Col md="8">
                  <CustomInput type="select" name="select" id="customerType">
                    {this.state.customer_types.map((item) => (
                      <option key={item.id} value={item.id}>
                        {item.name}
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
                  <span>Apt/Suite/Other</span>
                </Col>
                <Col md="8">
                  <Input type="text" id="aptSuiteOther" />
                </Col>
              </FormGroup>
              <FormGroup className="align-items-center" row>
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
                    defaultValue={null}
                    loadOptions={this.loadOptions}
                    onChange={(value) => {
                      if (value !== null) {
                        this.setState({
                          stateProvince: value.value,
                        });
                      } else
                        this.setState({
                          stateProvince: null,
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
              </FormGroup>
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
                  <span>Phone Number</span>
                </Col>
                <Col md="8">
                  <Input type="text" id="phoneNumber" />
                </Col>
              </FormGroup>
              <FormGroup className="align-items-center" row>
                <Col md="4">
                  <span>Phone Extension number</span>
                </Col>
                <Col md="8">
                  <Input type="text" id="phoneExtensionNumber" />
                </Col>
              </FormGroup>
              <FormGroup className="align-items-center" row>
                <Col md="4">
                  <span>Alternate Phone</span>
                </Col>
                <Col md="8">
                  <Input type="text" id="alternatePhone" />
                </Col>
              </FormGroup>
            </Form>
          </div>
          <div style={{ width: "50%" }}>
            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>Alternate Phone Extension number</span>
              </Col>
              <Col md="8">
                <Input type="text" id="alternatePhoneExtensionNumber" />
              </Col>
            </FormGroup>

            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>Fax</span>
              </Col>
              <Col md="8">
                <Input type="text" id="fax" />
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
                <span>Website</span>
              </Col>
              <Col md="8">
                <Input type="text" id="webSite" />
              </Col>
            </FormGroup>
            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>Contact</span>
              </Col>
              <Col md="8">
                <Input type="text" id="contact" />
              </Col>
            </FormGroup>
            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>Notes</span>
              </Col>
              <Col md="8">
                <Input type="textarea" id="notes" maxLength="1000" />
              </Col>
            </FormGroup>
            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>Motor Carrier Number</span>
              </Col>
              <Col md="8">
                <Input type="text" id="motorCarrierNumber" />
              </Col>
            </FormGroup>
            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>Tax ID (EIN#)</span>
              </Col>
              <Col md="8">
                <Input type="text" id="taxId" />
              </Col>
            </FormGroup>
          </div>
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={() => this.newCompany()}>
            Accept
          </Button>
        </ModalFooter>
      </Modal>
    );
  }
}
export default NewCompany;
