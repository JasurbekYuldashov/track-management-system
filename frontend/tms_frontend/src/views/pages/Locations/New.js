import React from "react";
import {
    // Form,
    Form,
    Button,
    FormGroup,
    Input,
    // Label,
    CustomInput,
    // Row,
    Col,
    Card,
    CardBody,
    // CardTitle,
    CardHeader,
    // Button
} from "reactstrap";
import * as Icon from "react-feather";
import { connect } from "react-redux";
import {
    // ToastContainer,
    toast,
    // Slide,
    // Zoom,
    Flip,
    // Bounce,
} from "react-toastify";

class NewLocation extends React.Component {
    state = {
        states: [],
        timeZones: [],
        logoFileId: null,
        currentState: null,
        parentAnsi: null,
    };
    newLocation = () => {
        let data = {
            name: document.querySelector("#name").value,
            parentAnsi: this.state.parentAnsi,
            parentTimeZone: document.querySelector("#timeZone").value,
        };
        fetch(process.env.REACT_APP_BASE_URL + "/location/new", {
            headers: {
                Authorization: this.props.token,
                "Content-Type": "application/json",
            },
            method: "POST",
            body: JSON.stringify(data),
        }).then((res) => {
            if (res.ok) {
                toast.success("Location successfuly added", {
                    transition: Flip,
                });
                window.history.back();
            } else {
                toast.error("Something went wrong", { transition: Flip });
                res.text();
            }
        });
    };

    componentDidUpdate(prevProps, prevState) {
        if (this.state.currentState !== prevState.currentState) {
            let state = this.state.states.find(
                (item) => item.id === this.state.currentState
            );
            let timeZones = [];
            timeZones.push(state.firstTimeZone);
            if (state.secondTimeZone) timeZones.push(state.secondTimeZone);
            this.setState({
                timeZones,
                parentAnsi: state.ansi,
            });
        }
    }
    componentDidMount() {
        fetch(process.env.REACT_APP_BASE_URL + "/location/context", {
            headers: {
                Authorization: this.props.token,
            },
        })
            .then((res) => res.json())
            .then((data) => {
                this.setState({
                    states: data.states,
                    currentState: data.states[0].id,
                });
            });
    }
    render() {
        return (
            <Card>
                <CardHeader>
                    <h3 className="mb-0">Adding a new Location</h3>
                </CardHeader>
                <CardBody className="d-flex">
                    <div style={{ marginRight: 20, flex: 1 }}>
                        <Form>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>Name</span>
                                </Col>
                                <Col md="8">
                                    <Input type="text" id="name" />
                                </Col>
                            </FormGroup>
                            <FormGroup className="align-items-center" row>
                                <Col md="4">
                                    <span>State*</span>
                                </Col>
                                <Col md="8">
                                    <CustomInput
                                        type="select"
                                        name="select"
                                        id="state"
                                        value={this.state.currentState}
                                        onChange={(val) =>
                                            this.setState({
                                                currentState: parseInt(
                                                    val.target.value
                                                ),
                                            })
                                        }
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
                                    <span>Time Zone</span>
                                </Col>
                                <Col md="8">
                                    <CustomInput
                                        type="select"
                                        name="select"
                                        id="timeZone"
                                    >
                                        {this.state.timeZones.map((item, i) => (
                                            <option key={item} value={i + 1}>
                                                {item}
                                            </option>
                                        ))}
                                    </CustomInput>
                                </Col>
                            </FormGroup>
                        </Form>
                        <Button
                            color="success"
                            className="d-flex align-items-center"
                            type="button"
                            onClick={() => this.newLocation()}
                        >
                            <Icon.Check size={22} /> Save location
                        </Button>
                    </div>
                    <div style={{ width: "50%" }}></div>
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
export default connect(mapStateToProps)(NewLocation);
