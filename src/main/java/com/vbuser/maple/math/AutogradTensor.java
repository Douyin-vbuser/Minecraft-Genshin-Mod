package com.vbuser.maple.math;

import java.util.*;

@SuppressWarnings("unused")
public class AutogradTensor extends Tensor {
    public AutogradTensor gradient;
    public Function backward;

    public interface Function {
        void apply();
    }

    public AutogradTensor(double[] data, int[] shape) {
        super(data, shape);
        this.gradient = null;
        this.backward = null;
    }

    public void backward() {
        if (this.gradient == null) {
            this.gradient = new AutogradTensor(new double[this.data.length], this.shape);
            Arrays.fill(this.gradient.data, 1.0);
        }

        List<AutogradTensor> topo = new ArrayList<>();
        Set<AutogradTensor> visited = new HashSet<>();
        buildTopo(this, visited, topo);

        for (int i = topo.size() - 1; i >= 0; i--) {
            AutogradTensor t = topo.get(i);
            if (t.backward != null) {
                t.backward.apply();
            }
        }
    }

    private void buildTopo(AutogradTensor tensor, Set<AutogradTensor> visited, List<AutogradTensor> topo) {
        if (!visited.contains(tensor)) {
            visited.add(tensor);
            if (tensor.backward != null) {
                buildTopo(tensor, visited, topo);
            }
            topo.add(tensor);
        }
    }

    public AutogradTensor add(AutogradTensor other) {
        AutogradTensor[] bc = broadcast(this,other);
        AutogradTensor result = new AutogradTensor(new double[bc[0].data.length], bc[0].shape);
        for (int i = 0; i < bc[0].data.length; i++) {
            result.data[i] = bc[0].data[i] + bc[1].data[i];
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < bc[0].data.length; i++) {
                    bc[0].gradient.data[i] += result.gradient.data[i];
                }
            }
            if (bc[1].gradient != null) {
                for (int i = 0; i < bc[1].data.length; i++) {
                    bc[1].gradient.data[i] += result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public AutogradTensor sub(AutogradTensor other) {
        AutogradTensor[] bc = broadcast(this,other);
        AutogradTensor result = new AutogradTensor(new double[bc[0].data.length], bc[0].shape);
        for (int i = 0; i < bc[0].data.length; i++) {
            result.data[i] = bc[0].data[i] - bc[1].data[i];
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < bc[0].data.length; i++) {
                    bc[0].gradient.data[i] += result.gradient.data[i];
                }
            }
            if (bc[1].gradient != null) {
                for (int i = 0; i < bc[1].data.length; i++) {
                    bc[1].gradient.data[i] -= result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public AutogradTensor mul(AutogradTensor other) {
        AutogradTensor[] bc = broadcast(this,other);
        AutogradTensor result = new AutogradTensor(new double[bc[0].data.length], bc[0].shape);
        for (int i = 0; i < bc[0].data.length; i++) {
            result.data[i] = bc[0].data[i] * bc[1].data[i];
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < bc[0].data.length; i++) {
                    bc[0].gradient.data[i] += bc[1].data[i] * result.gradient.data[i];
                }
            }
            if (bc[1].gradient != null) {
                for (int i = 0; i < bc[1].data.length; i++) {
                    bc[1].gradient.data[i] += bc[0].data[i] * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public AutogradTensor div(AutogradTensor other) {
        AutogradTensor[] bc = broadcast(this,other);
        AutogradTensor result = new AutogradTensor(new double[bc[0].data.length], bc[0].shape);
        for (int i = 0; i < bc[0].data.length; i++) {
            result.data[i] = bc[0].data[i] / bc[1].data[i];
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < bc[0].data.length; i++) {
                    bc[0].gradient.data[i] += result.gradient.data[i] / bc[1].data[i];
                }
            }
            if (bc[1].gradient != null) {
                for (int i = 0; i < bc[1].data.length; i++) {
                    bc[1].gradient.data[i] -= result.gradient.data[i] * bc[0].data[i] / (bc[1].data[i] * bc[1].data[i]);
                }
            }
        };

        return result;
    }

    public AutogradTensor pow(double exponent) {
        AutogradTensor result = new AutogradTensor(new double[this.data.length], this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.pow(this.data[i], exponent);
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < this.data.length; i++) {
                    this.gradient.data[i] += exponent * Math.pow(this.data[i], exponent - 1) * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public static AutogradTensor sum(AutogradTensor tensor) {
        double sum = 0;
        for (double value : tensor.data) {
            sum += value;
        }
        AutogradTensor result = new AutogradTensor(new double[]{sum}, new int[]{1});

        result.backward = () -> {
            if (tensor.gradient != null) {
                for (int i = 0; i < tensor.data.length; i++) {
                    tensor.gradient.data[i] += result.gradient.data[0];
                }
            }
        };

        return result;
    }

    public AutogradTensor mean() {
        double sum = 0;
        for (double value : this.data) {
            sum += value;
        }
        double mean = sum / this.data.length;

        AutogradTensor result = new AutogradTensor(new double[]{mean}, new int[]{1});

        result.backward = () -> {
            if (this.gradient != null) {
                double gradientValue = result.gradient.data[0] / this.data.length;
                for (int i = 0; i < this.data.length; i++) {
                    this.gradient.data[i] += gradientValue;
                }
            }
        };

        return result;
    }

    public AutogradTensor log() {
        AutogradTensor result = new AutogradTensor(new double[this.data.length], this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.log(this.data[i]);
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < this.data.length; i++) {
                    this.gradient.data[i] += result.gradient.data[i] / this.data[i];
                }
            }
        };

        return result;
    }

    public AutogradTensor getGradient() {
        return this.gradient;
    }

    public AutogradTensor relu() {
        AutogradTensor result = new AutogradTensor(new double[this.data.length], this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.max(0, this.data[i]);
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < this.data.length; i++) {
                    this.gradient.data[i] += (this.data[i] > 0 ? 1 : 0) * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public AutogradTensor sigmoid() {
        AutogradTensor result = new AutogradTensor(new double[this.data.length], this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = 1.0 / (1.0 + Math.exp(-this.data[i]));
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < this.data.length; i++) {
                    double sigmoidX = result.data[i];
                    this.gradient.data[i] += sigmoidX * (1 - sigmoidX) * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public AutogradTensor tanh() {
        AutogradTensor result = new AutogradTensor(new double[this.data.length], this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.tanh(this.data[i]);
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < this.data.length; i++) {
                    double tanhX = result.data[i];
                    this.gradient.data[i] += (1 - tanhX * tanhX) * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public AutogradTensor leakyRelu(double alpha) {
        AutogradTensor result = new AutogradTensor(new double[this.data.length], this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = this.data[i] > 0 ? this.data[i] : alpha * this.data[i];
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < this.data.length; i++) {
                    this.gradient.data[i] += (this.data[i] > 0 ? 1 : alpha) * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public AutogradTensor softmax() {
        AutogradTensor result = new AutogradTensor(new double[this.data.length], this.shape);

        double maxVal = Double.NEGATIVE_INFINITY;
        for (double val : this.data) {
            maxVal = Math.max(maxVal, val);
        }

        double sum = 0.0;
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.exp(this.data[i] - maxVal);
            sum += result.data[i];
        }

        for (int i = 0; i < this.data.length; i++) {
            result.data[i] /= sum;
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < this.data.length; i++) {
                    double si = result.data[i];
                    for (int j = 0; j < this.data.length; j++) {
                        double sj = result.data[j];
                        this.gradient.data[i] += si * ((i == j ? 1 : 0) - sj) * result.gradient.data[j];
                    }
                }
            }
        };

        return result;
    }

    public AutogradTensor elu(double alpha) {
        AutogradTensor result = new AutogradTensor(new double[this.data.length], this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = this.data[i] > 0 ?
                    this.data[i] : alpha * (Math.exp(this.data[i]) - 1);
        }

        result.backward = () -> {
            if (this.gradient != null) {
                for (int i = 0; i < this.data.length; i++) {
                    double derivative = this.data[i] > 0 ?
                            1 : alpha * Math.exp(this.data[i]);
                    this.gradient.data[i] += derivative * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public AutogradTensor slice(int start, int end) {
        if (start < 0 || end > this.data.length || start >= end) {
            throw new IllegalArgumentException("Invalid slice range");
        }

        int sliceLength = end - start;
        double[] slicedData = Arrays.copyOfRange(this.data, start, end);

        int[] newShape;
        if (this.shape.length > 1) {
            newShape = Arrays.copyOf(this.shape, this.shape.length);
            newShape[0] = sliceLength / (this.data.length / this.shape[0]);
        } else {
            newShape = new int[]{sliceLength};
        }

        AutogradTensor result = new AutogradTensor(slicedData, newShape);

        result.backward = () -> {
            if (this.gradient == null) {
                this.gradient = new AutogradTensor(new double[this.data.length], this.shape);
            }
            System.arraycopy(result.gradient.data, 0, this.gradient.data, start, sliceLength);
        };

        return result;
    }

    public static AutogradTensor[] broadcast(AutogradTensor a, AutogradTensor b) {
        Tensor[] broadcasted = Tensor.broadcast(a, b);

        AutogradTensor[] result = new AutogradTensor[2];

        result[0] = new AutogradTensor(broadcasted[0].data, broadcasted[0].getShape());
        result[1] = new AutogradTensor(broadcasted[1].data, broadcasted[1].getShape());

        result[0].gradient = (a.gradient != null) ?
                new AutogradTensor(new double[result[0].data.length], result[0].getShape()) : null;
        result[1].gradient = (b.gradient != null) ?
                new AutogradTensor(new double[result[1].data.length], result[1].getShape()) : null;

        result[0].backward = () -> {
            if (a.gradient != null && result[0].gradient != null) {
                for (int i = 0; i < a.data.length; i++) {
                    int[] indices = a.getIndices(i);
                    int broadcastedIndex = result[0].getIndex(indices);
                    a.gradient.data[i] += result[0].gradient.data[broadcastedIndex];
                }
            }
        };

        result[1].backward = () -> {
            if (b.gradient != null && result[1].gradient != null) {
                for (int i = 0; i < b.data.length; i++) {
                    int[] indices = b.getIndices(i);
                    int broadcastedIndex = result[1].getIndex(indices);
                    b.gradient.data[i] += result[1].gradient.data[broadcastedIndex];
                }
            }
        };

        return result;
    }
}
