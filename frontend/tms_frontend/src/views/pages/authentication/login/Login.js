import React from "react";
import {
    Button,
    Card,
    CardBody,
    Row,
    Col,
    Form,
    FormGroup,
    Input,
} from "reactstrap";
import { connect } from "react-redux";
import { User, Lock } from "react-feather";
import * as actions from "../../../../redux/actions/auth/loginActions";
import "../../../../assets/scss/pages/authentication.scss";
import InputPasswordToggle from "../../../../components/@vuexy/input-password-toggle";
class Login extends React.Component {
    state = {
        username: "",
        password: "",
    };

    render() {
        return (
            <Row className="m-0 justify-content-center">
                <Col
                    sm="6"
                    xl="3"
                    lg="3"
                    md="6"
                    className="d-flex justify-content-center"
                >
                    <Card className="bg-authentication login-card rounded-0 mb-0 w-100">
                        <Row className="m-0">
                            {/* <Col
                lg="6"
                className="d-lg-block d-none text-center align-self-center px-5 py-0"
              >
                <img
                  src={loginImg}
                  alt="loginImg"
                  style={{ maxWidth: "100%" }}
                />
              </Col> */}
                            <Col lg="12" md="12" className="p-0">
                                <Card className="rounded-0 mb-0 px-2">
                                    <CardBody>
                                        <h4>Login</h4>
                                        <p>Authentication page</p>
                                        <Form
                                            onSubmit={(e) => e.preventDefault()}
                                        >
                                            <FormGroup className="form-label-group position-relative has-icon-left">
                                                <Input
                                                    type="text"
                                                    placeholder="username"
                                                    onChange={(e) =>
                                                        this.setState({
                                                            username:
                                                                e.target.value,
                                                        })
                                                    }
                                                />
                                                <div className="form-control-position">
                                                    <User size={15} />
                                                </div>
                                            </FormGroup>
                                            <FormGroup className="form-label-group position-relative has-icon-left">
                                                <InputPasswordToggle
                                                    className="mb-2"
                                                    label="Password"
                                                    onChange={(e) =>
                                                        this.setState({
                                                            password:
                                                                e.target.value,
                                                        })
                                                    }
                                                />
                                                <div className="form-control-position">
                                                    <Lock size={15} />
                                                </div>
                                            </FormGroup>

                                            <div className="d-flex justify-content-between">
                                                {/* <Button.Ripple color="primary" outline>
                          Register
                        </Button.Ripple> */}
                                                <Button.Ripple
                                                    color="primary"
                                                    type="submit"
                                                    onClick={() =>
                                                        this.props.log_in(
                                                            this.state.username,
                                                            this.state.password
                                                        )
                                                    }
                                                >
                                                    Login
                                                </Button.Ripple>
                                            </div>
                                        </Form>
                                    </CardBody>
                                </Card>
                            </Col>
                        </Row>
                    </Card>
                </Col>
            </Row>
        );
    }
}
const mapStateToProps = (state) => {
    return {
        cookiesAuthFinished: state.auth.login.cookiesAuthFinished,
    };
};
export default connect(mapStateToProps, actions)(Login);
