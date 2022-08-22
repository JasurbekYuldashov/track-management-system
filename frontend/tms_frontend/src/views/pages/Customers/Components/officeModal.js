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
class OfficeModal extends React.Component {
  state = {
    consignee: null,
    consigneeName: "",
    consigneeSelected: null,
  };

  newOffice = () => {
    let sendingData = {
      parentId: parseInt(this.props.parentId),
      stateProvinceId: parseInt(document.querySelector("#office-state").value),
      city: document.querySelector("#office-city").value,
      street: document.querySelector("#office-street").value,
    };
    fetch("/company/child", {
      headers: {
        Authorization: this.props.token,
        "Content-Type": "application/json",
      },
      method: "POST",
      body: JSON.stringify(sendingData),
    })
      .then((res) => res.json())
      .then((data) => {
        this.props.addOffice(
          parseInt(document.querySelector("#office-state").value),
          document.querySelector("#office-city").value,
          data.id
        );
      });
  };
  render() {
    return (
      <Modal
        isOpen={this.props.modal}
        toggle={this.props.toggleDeliveryModal}
        className={this.props.className}
        backdrop="static"
      >
        <ModalHeader toggle={() => this.props.toggleOfficeModal(null)}>
          New office
        </ModalHeader>
        <ModalBody>
          <Form>
            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>State*</span>
              </Col>
              <Col md="8">
                <CustomInput type="select" name="select" id="office-state">
                  {this.props.states.map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.name}
                    </option>
                  ))}
                </CustomInput>
              </Col>
            </FormGroup>
            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>City*</span>
              </Col>
              <Col md="8">
                <Input type="text" id="office-city" />
              </Col>
            </FormGroup>
            <FormGroup className="align-items-center" row>
              <Col md="4">
                <span>Street</span>
              </Col>
              <Col md="8">
                <Input type="text" id="office-street" />
              </Col>
            </FormGroup>
          </Form>
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={() => this.newOffice()}>
            Accept
          </Button>
        </ModalFooter>
      </Modal>
    );
  }
}
export default OfficeModal;
