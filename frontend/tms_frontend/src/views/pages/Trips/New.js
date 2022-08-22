import React from "react";
import {
  // Form,
  Form,
  Button,
  FormGroup,
  Input,
  Col,
  Card,
  CardBody,
  CardHeader,
  // Button
} from "reactstrap";
import Table, { Thead, Tbody, Tr, Th, Td } from "react-row-select-table";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import { toast, Flip } from "react-toastify";
import Select from "react-select";
import AsyncSelect from "react-select/async";
import { Spin } from "antd";
import { LoadingOutlined } from "@ant-design/icons";
let choosedLoadsByNumber = [];
class NewTrip extends React.Component {
  state = {
    states: [],
    drivers: [],
    driver: null,
    driverId: null,
    secondDriver: null,
    secondDrivers: [],
    ownedCompanies: [],
    loads: [],
    truckId: null,
    truckInfo: null,
    loading: true,
  };
  chooseLoad = (value) => {
    choosedLoadsByNumber = value;
  };
  newTrip = () => {
    let loads = [];
    choosedLoadsByNumber.forEach((item) => {
      loads.push(this.state.loads[item].id);
    });
    let secondDriver =
      this.state.secondDriver == null ? null : this.state.secondDriver.value;
    let data = {
      driverId: this.state.driverId,
      loadIds: loads,
      truckId: this.state.truckId,
      driverInstructions: document.querySelector("#driverInstructions").value,
      secondDriverId: secondDriver,
    };
    fetch("/trip/new", {
      headers: {
        Authorization: this.props.token,
        "Content-Type": "application/json",
      },
      method: "POST",
      body: JSON.stringify(data),
    }).then((res) => {
      if (res.ok) {
        toast.success("Trip successfuly added", { transition: Flip });
        window.history.back();
      } else {
        toast.error("Something went wrong", { transition: Flip });
        res.text();
      }
    });
  };

  loadOptions = (inputValue, callback) => {
    fetch(`/unit/search_by_number?q=${inputValue}`, {
        headers: {
            Authorization: this.props.token,
            "Content-Type": "application/json",
        },
        method: "GET",
    })
      .then((res) => res.json())
      .then((data) =>
        callback(
          data.map((el) => {
            return {
              ...el,
              value: el.id,
              label: el.number,
            };
          })
        )
      );
  };
  componentDidUpdate(prevProps, prevState) {
    if (prevState.truckInfo !== this.state.truckInfo) {
      if (this.state.truckInfo && this.state.truckInfo.driverId) {
        let result = this.state.drivers.filter((obj) => {
          return obj.value === parseInt(this.state.truckInfo.driverId);
        });
        if (result)
          this.setState({
            driver: result[0],
            driverId: this.state.truckInfo.driverId,
          });
      }
      if (this.state.truckInfo && this.state.truckInfo.secondDriverId) {
        let result = this.state.drivers.filter((obj) => {
          return obj.value === parseInt(this.state.truckInfo.secondDriverId);
        });
        if (result) this.setState({ secondDriver: result[0] });
      }
    }
  }

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
    fetch("/trip/context", {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        this.setState({
          drivers: data.drivers,
          ownedCompanies: data.owned_companies,
        });
        let drivers = [];
        data.drivers.forEach((el) => {
          let elToShow = {
            value: el.id,
            label: el.lastName + " " + el.firstName,
          };
          drivers.push(elToShow);
        });
        this.setState({
          drivers: drivers,
        });
      });
    fetch("/load/list?sort=id,DESC&size=10000", {
      headers: {
        Authorization: this.props.token,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        this.setState({
          loads: data.content,
          loading: false,
        });
      });
  }
  render() {
    return (
      <Card>
        <CardHeader>
          <h3 className="mb-0">Adding a new Trip</h3>
        </CardHeader>
        <CardBody>
          {this.state.loading ? (
            <Spin
              indicator={<LoadingOutlined style={{ fontSize: 44 }} spin />}
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
                <div style={{ width: "50%", marginRight: 20 }}>
                  <FormGroup className="align-items-center" row>
                    <Col md="4">
                      <span>Truck*</span>
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
                              truckId: value.value,
                              truckInfo: value,
                            });
                          } else
                            this.setState({
                              truckId: null,
                              truckInfo: null,
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
                  <FormGroup row>
                    <Col md="4">
                      <span>Driver Instructions</span>
                    </Col>
                    <Col md="8">
                      <Input
                        type="textarea"
                        id="driverInstructions"
                        placeholder=""
                        maxLength="1000"
                        rows="3"
                      />
                    </Col>
                  </FormGroup>
                </div>
                <div style={{ flex: 1 }}>
                  <FormGroup className="align-items-center" row>
                    <Col md="4">
                      <span>Driver*</span>
                    </Col>
                    <Col md="8">
                      <Select
                        className="React"
                        classNamePrefix="select"
                        name="color"
                        options={this.state.drivers}
                        value={this.state.driver}
                        isClearable={true}
                        onChange={(value) => {
                          if (value !== null) {
                            this.setState({
                              driverId: value.value,
                              driver: value,
                            });
                          } else {
                            this.setState({
                              driverId: null,
                              driver: null,
                            });
                          }
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
                      <span>Second driver(optimal)</span>
                    </Col>
                    <Col md="8">
                      <Select
                        className="React"
                        classNamePrefix="select"
                        name="color"
                        value={this.state.secondDriver}
                        options={this.state.drivers}
                        isClearable={true}
                        onChange={(val) => {
                          if (val == null) {
                            this.setState({
                              secondDriver: null,
                            });
                          } else {
                            this.setState({
                              secondDriver: val,
                            });
                          }
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
                </div>
              </Form>
              <div className="mt-3"></div>
              <Table value={[1, 2]} onCheck={(value) => this.chooseLoad(value)}>
                <Thead>
                  <Tr>
                    <Th>Custom Load Number</Th>
                    <Th>Customer</Th>
                    <Th>Pickup</Th>
                    <Th>Delivery</Th>
                    <Th>From</Th>
                    <Th>To</Th>
                  </Tr>
                </Thead>
                <Tbody>
                  {this.state.loads.map((item) => {
                    return (
                      <Tr key={item.id}>
                        <Td>{item.customLoadNumber}</Td>
                        <Td>{item.customer}</Td>
                        <Td>{item.pickupDateFormatted}</Td>
                        <Td>{item.deliveryDateFormatted}</Td>
                        <Td>{item.from}</Td>
                        <Td>{item.to}</Td>
                      </Tr>
                    );
                  })}
                </Tbody>
              </Table>
              <Button
                color="success"
                className="d-flex align-items-center mt-4"
                type="button"
                onClick={() => this.newTrip()}
              >
                <Icon.Check size={22} /> Save Trip
              </Button>
            </>
          )}
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
export default connect(mapStateToProps)(NewTrip);
